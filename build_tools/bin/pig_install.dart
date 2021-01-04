#! /usr/bin/env dcli

import 'dart:io';

import 'package:pigation/src/pigation_settings.dart';
import 'package:pigation/src/environment.dart';
import 'package:dcli/dcli.dart';
// import 'package:nginx_le_shared/nginx_le_shared.dart' hide packageVersion;
import 'package:pigation/src/version/version.g.dart';

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
PigationSettings settings;

/// Run thi install script from the directory the zip file was exanded into.
///
void main(List<String> args) {
  var parser = ArgParser();
  parser.addFlag('debug',
      abbr: 'd', defaultsTo: false, help: 'Outputs verbose logging.');

  var results = parser.parse(args);
  var debug = results['debug'] as bool;
  Settings().setVerbose(enabled: debug);

  // if (!Shell.current.isPrivilegedUser) {
  //   printerr(red('You must run install as the root user'));
  //   print(r'run: sudo env "PATH=$PATH" pig_install');
  //   exit(1);
  // }

  var installSrcDir = join('versions', packageVersion);

  // we start by re-unziping the zip file so we ensure we do it into a clean directory.
  if (exists(installSrcDir)) {
    deleteDir(installSrcDir);
  }

  unzip(installSrcDir);

  if (!exists(join(installSrcDir, 'opt'))) {
    printerr(red(
        "Didn't find the opt directory. install must be run from the directory you expanded the $zipFilename into."));
    exit(1);
  }

  settings = PigationSettings.load();

  settings.hostname = ask('hostname',
      defaultValue: settings.hostname,
      required: true,
      validator: Ask.alphaNumeric);
  settings.domain = ask('domain',
      defaultValue: settings.domain, required: true, validator: Ask.fqdn);
  settings.tld = ask('tld',
      defaultValue: settings.tld, required: true, validator: Ask.alphaNumeric);

  settings.smtpHost =
      ask('SMTP Host', defaultValue: settings.smtpHost, required: true);

  settings.smtpPort =
      int.tryParse(ask('SMTP Port', defaultValue: '${settings.smtpPort}'));
  // validator: Ask.integer));

  settings.save();
  setEnvironment(settings);

  install(installSrcDir, settings);
}

String get zipFilename => 'install_pigation-$packageVersion.zip';

void unzip(String installSrcDir) {
  // print('Downloading java zip');
  //
  // fetch(
  //     url: 'https://github.com/bsutton/IrrigationForPi/assets/$zipFilename',
  //     saveToPath: join(installSrcDir, zipFilename));

  print(green('Unzipping into a clean directory.'));

  if (!exists(zipFilename)) {
    printerr(red('$zipFilename not found'));
    printerr(red(
        'pig_install must be run from the directory where $zipFilename is located'));
    exit(1);
  }
  'unzip -o $zipFilename'.run;

  // fix permissions
  'chown -R ${user}:${user} *'.start(privileged: true);
}

void install(String installSrc, PigationSettings settings) {
  print(green('Installing Pigation'));

  var pigationDir = join('/', 'opt', 'pigation');
  if (!exists(pigationDir)) {
    createDir(pigationDir, recursive: true);
  }

  var include = join('/', 'opt', 'nginx', 'include');
  if (!exists(include)) {
    createDir(include, recursive: true);
  }

  // rename the war to ROOT.war
  var war = find('*.war', root: installSrc).toList().first;
  var rootWar = join(dirname(war), 'ROOT.war');
  move(war, rootWar, overwrite: true);

  /// assumes we are running from the directory the zip was exanded into.
  copyTree(join(installSrc, 'opt'), '/opt', recursive: true, overwrite: true);

  /// allows us to re-run the install
  move(rootWar, war);

  // move from the versioned directory into the active directory.
  copy(join(installSrc, 'docker-compose.yaml'), pigationDir, overwrite: true);

  // copy the executables in.
  /// removed as the executables have to be compiled on the arm.
  /// copyTree(join(installSrc, 'bin'), pigationDir, overwrite: true);

  // create the pigation log directory
  'mkdir -p /var/log/tomcat/pigation'.start(privileged: true);

  'apt update'.start(privileged: true);

  setTimezone();

  // install docker:
  installDocker();

  print(green('Stopping Pigation'));
  'pig_stop'.start( workingDirectory: pigationDir);

  // pull the docker containers
  print(orange('Pulling the required containers'));
  print('PATH: $PATH');
  print('path to ${which('docker-compose').path}');
  'docker-compose pull'.start(workingDirectory: pigationDir);

  print(orange('Installing nginx-le cli components'));

  installCliTools();

  print(green('The containers will to be started to finalise configuration.'));
  // delete the old named containers
  deleteContainer(name: 'nginx-le');
  deleteContainer(name: 'tomcat');

  'docker-compose up -d'.start(workingDirectory: pigationDir);

  /// we have just created new container so...
  // Containers().flushCache();

  cleanupPermissions();

  print('Settings saved to ${PigationSettings.path}');

  print('Install complete and Pigation is running.');
  print('');

  print("Run ${green('start/stop')} to start/stop the containers");
}

void deleteContainer({String name}) {
  // var target = Containers()
  //     .containers()
  //     .firstWhere((container) => container.names == name, orElse: () => null);
  // if (target != null) {
  //   target.stop();
  //   target.delete();
  // } else {
  //   print(orange('No existing container for $name found.'));
//   }
}

void cleanupPermissions() {
  'chown -R ${user}:${user} *'.start(privileged: true);
  'chown -R ${user}:${user} $HOME/.*'.start(privileged: true);
}

String get user {
  var user = env['SUDO_USER'];
  user ??= env['USER'];
  return user;
}

void setTimezone() {
  var TZ = 'Australia/Melbourne';

  'ln -snf /usr/share/zoneinfo/$TZ /etc/localtime'.run;
  '/etc/timezone'.write('$TZ');
  'apt install -y  tzdata'.start(privileged: true, runInShell: true);
}

void installCliTools() {
  'apt install -y --no-install-recommends  wget ca-certificates gnupg2 unzip'
      .start(privileged: true, runInShell: true);
  print('New path is: $PATH');
  print('pub path: ${which('pub').path}');

  // 'pub global activate nginx_le'.start(privileged: true);

  print(orange("Don't forget to install nginx-le."));
  print('Run: pub global activate nginx_le');
}

void installDocker() {
  print(orange('Installing docker'));
  // 'apt remove docker docker-engine docker.io containerd runc'.start(privileged: true);

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
  var key = FileSync.tempFile();
  'curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o $key'.run;
  'apt-key add $key'.run;

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
  'usermod -aG docker ${user}'.start(privileged: true);

  print('Installing docker-compose');
  'apt install --no-install-recommends -y  docker-compose'
      .start(privileged: true, runInShell: true);
}

class NoLocalHost extends AskValidator {
  @override
  String validate(String line) {
    if (line.trim() == 'localhost') {
      throw AskValidatorException(
          "localhost won't work as MySql treats this as a socket connection rather than an IP connection. Use 127.0.0.1 instead");
    }
    return line;
  }
}
