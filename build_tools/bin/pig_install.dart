#! /usr/bin/env dcli

import 'dart:io';

import 'package:collection/collection.dart' show IterableExtension;
import 'package:docker2/docker2.dart';
import 'package:pigation/src/pigation_settings.dart';
import 'package:pigation/src/environment.dart';
import 'package:dcli/dcli.dart';
import 'package:pigation/src/version/version.g.dart' as v;

/// dcli script generated by:
/// dcli create install.dart
///
/// See
/// https://pub.dev/packages/dcli#-installing-tab-
///
/// For details on installing dcli.
///
///
///
PigationSettings? settings;

/// Run thi install script from the directory the zip file was exanded into.
///
void main(List<String> args) {
  print('pig_install ${v.packageVersion}');
  var parser = ArgParser();
  parser.addFlag('debug',
      abbr: 'd', defaultsTo: false, help: 'Outputs verbose logging.');

  var results = parser.parse(args);
  var debug = results['debug'] as bool;
  Settings().setVerbose(enabled: debug);

  if (!Shell.current.isPrivilegedUser) {
    printerr(red('You must run install as the root user'));
    print(
        'Please restart ${DartScript.self.exeName} using sudo: sudo env "PATH=\$PATH" pig_install');
    exit(1);
  }

  Shell.current.releasePrivileges();

  var zipFilePathTo = findZipFile();
  var expandIntoPathTo = join(pwd, 'versions');

  Shell.current.withPrivileges(() {
    // we start by re-unziping the zip file so we ensure we do it into a clean directory.
    if (exists(expandIntoPathTo)) {
      deleteDir(expandIntoPathTo);
    }
    createDir(expandIntoPathTo, recursive: true);

    'chown -R $user:$user $expandIntoPathTo'.run;
  });

  print('Expanding zip file to $expandIntoPathTo');

  unzip(zipFilePathTo, expandIntoPathTo);

  var optPathTo = join(expandIntoPathTo, v.packageVersion, 'opt');
  if (!exists(optPathTo)) {
    printerr(red('It appears that the unzip failed. Try again.'));
    exit(1);
  }

  settings = PigationSettings.load();

  settings!.hostname = ask('hostname',
      defaultValue: settings!.hostname,
      required: true,
      validator: Ask.alphaNumeric);
  settings!.domain = ask('domain',
      defaultValue: settings!.domain, required: true, validator: Ask.fqdn);
  settings!.tld = ask('tld',
      defaultValue: settings!.tld, required: true, validator: Ask.alphaNumeric);

  print(
      'Lets Encrypt requires an email address to send certificate notifications to.');
  settings!.email = ask('Email',
      defaultValue: settings!.email, required: true, validator: Ask.email);

  // print(
  //     'If you have access to an smtp server then pigation can send you email alerts.');
  // print(
  //     "If you don't have an smtp server then just enter the FQDN of your pi.");
  // settings.smtpHost =
  //     ask('SMTP Host', defaultValue: settings.smtpHost, required: true);

  // settings.smtpPort =
  //     int.tryParse(ask('SMTP Port', defaultValue: '${settings.smtpPort}'));
  // validator: Ask.integer));

  settings!.save();
  setEnvironment(settings!);

  install(join(expandIntoPathTo, v.packageVersion), settings);
}

String findZipFile() {
  var pathToZip = join(pwd, zipFilename);

  if (!exists(pathToZip)) {
    pathToZip = join(pwd, 'releases', zipFilename);

    if (!exists(pathToZip)) {
      // try local build path
      pathToZip = join(pwd, 'pigation', 'IrrigationForPi', 'build_tools',
          'releases', zipFilename);

      if (!exists(pathToZip)) {
        print(red(
            "Can't find the zip file $zipFilename. Place it in the current directory and try again."));
        exit(-1);
      }
    }
  }

  return pathToZip;
}

String get zipFilename => 'install_pigation-${v.packageVersion}.zip';

void unzip(String zipFilePathTo, String expandIntoPathTo) {
  // print('Downloading java zip');
  //
  // fetch(
  //     url: 'https://github.com/bsutton/IrrigationForPi/assets/$zipFilename',
  //     saveToPath: join(expandIntoPathTo, zipFilename));

  print(green('Unzipping into a clean directory.'));

  if (!exists(zipFilePathTo)) {
    printerr(red('$zipFilename not found'));
    printerr(red('Run pig_build and then try again.'));
    exit(1);
  }
  'unzip -o $zipFilePathTo'.start(workingDirectory: expandIntoPathTo);

  // fix permissions
  // 'chown -R ${user}:${user} *'
  //     .start(privileged: true, workingDirectory: expandIntoPathTo);
}

void install(String installSrc, PigationSettings? settings) {
  print(green('Installing Pigation'));

  var pigationDir = join('/', 'opt', 'pigation');
  if (!exists(pigationDir)) {
    createDir(pigationDir, recursive: true);
  }

  // move from the versioned directory into the active directory.
  copy(join(installSrc, 'docker-compose.yaml'), pigationDir, overwrite: true);

  Shell.current.withPrivileges(() {
    var include = join('/', 'opt', 'nginx', 'include');
    if (!exists(include)) {
      createDir(include, recursive: true);
    }

    // rename the war to ROOT.war
    var war = find('*.war', workingDirectory: installSrc).toList().first;
    var rootWar = join(dirname(war), 'ROOT.war');
    move(war, rootWar, overwrite: true);

    // Settings().setVerbose(enabled: true);

    /// assumes we are running from the directory the zip was exanded into.
    copyTree(join(installSrc, 'opt'), '/opt', recursive: true, overwrite: true);

    /// allows us to re-run the install
    move(rootWar, war);

    // create the pigation log directory
    if (!exists('/var/log/tomcat/pigation')) {
      createDir('/var/log/tomcat/pigation', recursive: true);
    }

    // copy the executables in.
    /// removed as the executables have to be compiled on the arm.
    /// copyTree(join(installSrc, 'bin'), pigationDir, overwrite: true);

    'apt update'.start(privileged: true);
  });

  setTimezone();

  // install docker:
  installDocker();

  print(green('Stopping Pigation'));

  /// we are running as sudo so pig_stop may not be on the path but it should be in
  /// pub-cache or the same directory as pig_install.
  /// scripts path to locate pig_stop as they should both be in the same dir.

  var pigStopPathTo = join(PubCache().pathToBin, 'pig_stop');

  if (!exists(pigStopPathTo)) {
    /// use scripts path to locate pig_stop as they should both be in the same dir.
    pigStopPathTo = join(dirname(DartScript.self.pathToScript), 'pig_stop');
  }
  pigStopPathTo.start(workingDirectory: pigationDir);

  // pull the docker containers
  print(orange('Pulling docker containers'));
  // print('path to ${which('docker-compose').path}');
  Shell.current.withPrivileges(() {
    'docker-compose pull'.start(workingDirectory: pigationDir);
  });

  print(orange('Installing nginx-le cli components'));

  installCliTools();

  Shell.current.withPrivileges(() {
    print(
        green('The containers will to be started to finalise configuration.'));
    // delete the old named containers
    deleteContainer(name: 'nginx-le');
    deleteContainer(name: 'tomcat');
    'docker-compose up -d'.start(workingDirectory: pigationDir);

    cleanupPermissions();
  });

  print('Settings saved to ${PigationSettings.path}');

  print('Install complete and Pigation is running.');
  print('');

  print("Run ${green('pig_start/pig_stop')} to start/stop the containers");
}

void deleteContainer({String? name}) {
  var target = Containers()
      .containers()
      .firstWhereOrNull((container) => container.name == name);
  if (target != null) {
    target.stop();
    target.delete();
  } else {
    print(orange('No existing container for $name found.'));
  }
}

void cleanupPermissions() {
  'chown -R $user:$user *'.start(privileged: true);
  'chown -R $user:$user $HOME/.*'.start(privileged: true);
}

String get user {
  var user = env['SUDO_USER'];
  user ??= env['USER'];
  return user!;
}

void setTimezone() {
  Shell.current.withPrivileges(() {
    var TZ = 'Australia/Melbourne';

    'ln -snf /usr/share/zoneinfo/$TZ /etc/localtime'.start(privileged: true);
    '/etc/timezone'.write('$TZ');
    'apt install -y  tzdata'.start(privileged: true, runInShell: true);
  });
}

void installCliTools() {
  'apt install -y --no-install-recommends  wget ca-certificates gnupg2 unzip'
      .start(privileged: true, runInShell: true);
  // print('New path is: $PATH');

  // 'pub global activate nginx_le'.start(privileged: true);

  // print(orange("Don't forget to install nginx-le."));
  // print('Run: pub global activate nginx_le');
}

void installDocker() {
  print(orange('Installing docker'));
  '''apt install 
  -y
  --no-install-recommends 
    apt-transport-https 
    curl 
    gnupg-agent 
    gnupg2 pass
    software-properties-common'''
      .replaceAll('\n', ' ')
      .start(privileged: true, runInShell: true);

  /// add the docker key
  withTempFile((key) {
    'curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o $key'.run;
    'apt-key add $key'.run;
  });

  'apt install --no-install-recommends -y docker.io'
      .start(privileged: true, runInShell: true);

  // set up docker permissions:
  var dockerGroup = read('/etc/group')
      .toList()
      .where((element) => element.contains('docker'))
      .toList();
  if (dockerGroup.isEmpty) {
    'groupadd docker'.start(privileged: true);
  }
  'usermod -aG docker $user'.start(privileged: true);

  print('Installing docker-compose');
  'apt install --no-install-recommends -y  docker-compose'
      .start(privileged: true, runInShell: true);

  'service docker start'.start(privileged: true);

  print(orange('Waiting for docker daemon to start'));

  while ('systemctl show --property ActiveState docker'.firstLine !=
      'ActiveState=active') {
    sleep(5);
    print('Waiting for docker daemon to start...');
  }
}
