#! /usr/bin/env dcli

import 'dart:io';

import 'package:dcli/dcli.dart';
import 'package:pub_release/pub_release.dart';
import 'package:pigation/src/version/version.g.dart' as v;

String? projectRoot;

/// You can manually run this when doing local testing.
///
///
void main(List<String> args) {
  // if (!Shell.current.isPrivilegedProcess) {
  //   printerr(
  //       'Please restart ${DartScript.self.exeName} using sudo: sudo env "PATH=\$PATH" ./pig_build');
  //   exit(1);
  // }

  Shell.current.releasePrivileges();

  var parser = ArgParser();

  print('pig_build ${v.packageVersion}');

  parser.addFlag('debug',
      abbr: 'd', defaultsTo: false, help: 'Enables debug output.');

  parser.addFlag('full',
      abbr: 'f',
      defaultsTo: true,
      help:
          'Does a full build including cloning the repo and installing the dev chain.');

  parser.addFlag('quick',
      abbr: 'q', defaultsTo: false, help: 'Skips rebuilding the war file.');

  parser.addFlag('current',
      abbr: 'c',
      defaultsTo: true,
      help: 'If passed the current pubspec version no. is used.');

  parser.addFlag('tools',
      abbr: 't',
      defaultsTo: true,
      help:
          'If passed then the java build tools are installed. Default to true.');

  var results = parser.parse(args);
  var quick = results['quick'] as bool;
  var debug = results['debug'] as bool;
  var full = results['full'] as bool;
  var current = results['current'] as bool;
  var tools = results['tools'] as bool?;

  var originalUser = env['SUDO_USER'] ?? env['USERNAME'] ?? 'root';
  withEnvironment(() {
    Settings().setVerbose(enabled: debug);
    print('debug=$debug');
    if (!quick) {
      print(orange('Use --quick to avoid repeating the java build phase'));
    }

    projectRoot = DartScript.self.pathToProjectRoot;

    print('Pub-cache in ${PubCache().pathTo}');

    if (DartScript.self.isPubGlobalActivated || DartScript.self.isCompiled) {
      projectRoot = join(pathToJavaProject, 'build_tools');
    }

    print('building in : ${truepath(pathToPigation)}');
    print('Project root: $projectRoot');
    if (full) {
      prepForBuild(tools!);
    }
    var zip = build(quick: quick, current: current);

    showCompletedMessage(zip);
  }, environment: {
    'USER': originalUser,
    'HOME': join(HOME, originalUser),
    'LOGNAME': originalUser
  });
}

String get pathToJavaProject => join(pathToPigation, 'IrrigationForPi');
String get pathToPigation =>
    join(DartProject.self.pathToProjectRoot, 'pigation');

/// clean the maven target directory unless we are running with quick

String get mvnTarget => join(join(pathToJavaProject, 'target'));

void prepForBuild(bool tools) {
  if (!exists(pathToJavaProject)) {
    print('Cloning project into $pathToJavaProject');

    Shell.current.withPrivileges(() {
      verbose(() => 'sudo user: ${env['USER']}');
      // create the directory and make certain we can write to it.
      createDir(pathToJavaProject, recursive: true);
      final user = Shell.current.loggedInUser;
      'chown -R $user:$user $pathToPigation'.run;
    }, allowUnprivileged: true);
    verbose(() => 'user: ${env['USER']}');
    'git clone https://github.com/bsutton/IrrigationForPi.git'
        .start(workingDirectory: pathToPigation);
  } else {
    print('Pulling latest version of project into $pathToJavaProject');
    verbose(() => 'user: ${env['USER']}');
    verbose(() => 'home: ${env['HOME']}');
    verbose(() => 'priviliged: ${Shell.current.isPrivilegedUser}');
    verbose(() => 'env $envs');

    'git pull'.start(workingDirectory: pathToJavaProject);
  }

  if (tools) {
    print('Installing build tools');

    'apt install --no-install-recommends -y openjdk-8-jdk-headless maven'
        .start(privileged: true, runInShell: true);
  } else {
    print('Build tools will not be installed as --no-tools specified.');
  }
}

String build({required bool quick, required bool current}) {
  /// clean the dart_tool target directory
  var target = join(projectRoot!, 'target');
  if (exists(target)) {
    deleteDir(target, recursive: true);
  }

  Version? selectedVersion;

  if (current) {
    print('Building the pigation installer');
    selectedVersion = Version.parse(v.packageVersion);
  } else {
    Version? currentVersion;
    var pathToPubspec = findPubSpec();
    if (findPubSpec() == null) {
      print(red(
          'pubspec.yaml not found you can only build the the current version. Pass the --current flag'));
      exit(-1);
    } else {
      var pubspec = PubSpec.fromFile(pathToPubspec!);

      currentVersion = pubspec.version;
      selectedVersion = askForVersion(currentVersion!);
      updateVersion(currentVersion, pubspec, pathToPubspec);
    }

    print(
        'Building the pigation installer, selected version ${selectedVersion.toString()}');
  }

  var versionDir = join(target, selectedVersion.toString());

  Shell.current.withPrivileges(() {
    if (!quick && exists(mvnTarget)) {
      deleteDir(mvnTarget, recursive: true);
    }

    createDir(versionDir, recursive: true);
    final user = Shell.current.loggedInUser;
    'chown -R $user:$user ${dirname(versionDir)}'.run;
  }, allowUnprivileged: true);

  if (!quick) {
    print('building pigation');

    buildWar(projectRoot);
  } else {
    print('Java build will be skipped as --quick specified');
  }

  createZipImage(versionDir, projectRoot!, mvnTarget);

  var zip = createZip(target);
  return zip;
}

void createZipImage(String versionDir, String projectRoot, String mvnTarget) {
  var include = join(versionDir, 'opt', 'nginx', 'include');
  if (!exists(include)) {
    createDir(include, recursive: true);
  }

  copyTree(join(projectRoot, 'config/nginx/include'), include, overwrite: true);

  var tomcat_config = join(versionDir, 'opt', 'tomcat', 'config');
  if (!exists(tomcat_config)) {
    createDir(tomcat_config, recursive: true);
  }
  copyTree(join(projectRoot, 'config/tomcat/config'), tomcat_config,
      overwrite: true);

  copy(join(projectRoot, 'config/docker-compose.yaml'), versionDir,
      overwrite: true);

  /// copy in the war.
  var srcWar =
      find('*.war', workingDirectory: mvnTarget, recursive: true).firstLine!;
  var webappDir = join(versionDir, 'opt', 'tomcat', 'webapps');
  if (!exists(webappDir)) {
    createDir(webappDir);
  }

  var warPath = join(webappDir, 'pigation.${v.packageVersion}.war');
  print('copy srcDir $srcWar to warPath: $warPath');
  copy(srcWar, warPath);
}

void showCompletedMessage(String zip) {
  print(orange('*' * 80));
  print('Build complete.');
  print('');

  print('Copy ${truepath(zip)} to the target machine and then run');
  print('run ${green('pig_install')}');
}

String createZip(String target) {
  var zip = join(
      projectRoot!, 'releases', 'install_pigation-${v.packageVersion}.zip');

  if (!exists(dirname(zip))) {
    createDir(dirname(zip), recursive: true);
  }
  if (exists(zip)) {
    delete(zip);
  }

  'zip  -r $zip *'.start(workingDirectory: target);
  return zip;
}

void buildWar(String? projectRoot) {
  print('building java code');
  //  -U forces an update of all snapshot jars
  'mvn -DskipTests install -U'.start(workingDirectory: pathToJavaProject);
}
