#! /usr/bin/env dcli

import 'dart:io';

import 'package:dcli/dcli.dart';
import 'package:dcli/posix.dart';
import 'package:docker2/docker2.dart';
import 'package:pigation/src/version/version.g.dart' as v;
import 'package:pub_release/pub_release.dart';

String projectRoot = DartProject.self.pathToProjectRoot;

String pathToJavaProject = join(projectRoot, '..');
// String get pathToPigation =>
//     join(DartProject.self.pathToProjectRoot, '..');

/// clean the maven target directory unless we are running with quick

String get mvnTarget => join(join(pathToJavaProject, 'target'));

/// pig_build build the pig install zip file.
///
/// pig_build operates differently depending on the target.
///
/// For local testing using `pig_build` which generates an install.zip
/// appropriate for running on your local machine
///
/// Use `pig_build --arm` to build an install.zip ready to be
/// deployed onto a raspberry pi.
///
///
void main(List<String> args) {
  // if (!Shell.current.isPrivilegedProcess) {
  //   printerr(
  //       'Please restart ${DartScript.self.exeName} using sudo: sudo env "PATH=\$PATH" ./pig_build');
  //   exit(1);
  // }

  Shell.current.releasePrivileges();

  final parser = ArgParser();

  print('pig_build ${v.packageVersion}');

  parser
    ..addFlag('debug', abbr: 'd', help: 'Enables debug output.')
    ..addFlag('full',
        abbr: 'f',
        defaultsTo: true,
        help: 'Does a full build including cloning the repo and installing '
            'the dev chain.')
    ..addFlag('quick', abbr: 'q', help: 'Skips rebuilding the war file.')
    ..addFlag('current',
        abbr: 'c',
        defaultsTo: true,
        help: 'If passed the current pubspec version no. is used.')
    ..addFlag('tools',
        abbr: 't',
        defaultsTo: true,
        help: 'If passed then the java build tools are installed. '
            'Default to true.')
    ..addFlag('arm',
        abbr: 'a',
        defaultsTo: true,
        help: 'Use this switch when creating an install.zip suitable to '
            'install on a raspberry pi');

  final results = parser.parse(args);
  final quick = results['quick'] as bool;
  final debug = results['debug'] as bool;
  final full = results['full'] as bool;
  final current = results['current'] as bool;
  final tools = results['tools'] as bool;
  // final docker = results['docker'] as bool;

  // if (docker) {
  //   pathToJavaProject = join(DartProject.self.pathToProjectRoot, '..');
  // } else {
  // pathToJavaProject = join(pathToPigation, 'IrrigationForPi');
  // }

  final originalUser = env['SUDO_USER'] ?? env['USERNAME'] ?? 'root';
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

    print('building in : ${truepath(pathToJavaProject)}');
    print('Project root: $projectRoot');
    if (full) {
      prepForBuild(tools: tools);
    }
    final zip = build(quick: quick, current: current);
    showCompletedMessage(zip);
  }, environment: {
    'USER': originalUser,
    'HOME': join(HOME, originalUser),
    'LOGNAME': originalUser
  });
}

void prepForBuild({required bool tools}) {
  if (!exists(pathToJavaProject)) {
    print('Cloning project into $pathToJavaProject');

    Shell.current.withPrivileges(() {
      verbose(() => 'sudo user: ${env['USER']}');
      // create the directory and make certain we can write to it.
      createDir(pathToJavaProject, recursive: true);
      final user = Shell.current.loggedInUser;
      chown(pathToPigation, user: user, group: user);
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
  final target = join(projectRoot, 'target');
  if (exists(target)) {
    deleteDir(target);
  }

  Version? selectedVersion;
  if (current) {
    selectedVersion = Version.parse(v.packageVersion);
  } else {
    Version? currentVersion;
    final pathToPubspec = findPubSpec();
    if (findPubSpec() == null) {
      print(red(
          'pubspec.yaml not found you can only build the the current version. '
          'Pass the --current flag'));
      exit(-1);
    } else {
      final pubspec = PubSpec.fromFile(pathToPubspec!);

      currentVersion = pubspec.version;
      selectedVersion = askForVersion(currentVersion!);
      updateVersion(currentVersion, pubspec, pathToPubspec);
    }

    print('Building the pigation installer, selected version '
        '$selectedVersion');
  }

  final versionDir = join(target, selectedVersion.toString());

  Shell.current.withPrivileges(() {
    if (!quick && exists(mvnTarget)) {
      deleteDir(mvnTarget);
    }

    createDir(versionDir, recursive: true);
    final user = Shell.current.loggedInUser;
    chown(user: user, group: user, dirname(versionDir));
  }, allowUnprivileged: true);

  if (!quick) {
    print('building pigation');

    buildWar(projectRoot);
  } else {
    print('Java build will be skipped as --quick specified');
  }

  createZipImage(versionDir, projectRoot, mvnTarget);
  final zip = createZip(target);
  return zip;
}

void createZipImage(String versionDir, String projectRoot, String mvnTarget) {
  final include = join(versionDir, 'opt', 'nginx', 'include');
  if (!exists(include)) {
    createDir(include, recursive: true);
  }

  copyTree(join(projectRoot, 'docker/nginx/include'), include, overwrite: true);

  final tomcatConfig = join(versionDir, 'opt', 'tomcat', 'config');
  if (!exists(tomcatConfig)) {
    createDir(tomcatConfig, recursive: true);
  }
  copyTree(join(projectRoot, 'docker/tomcat/config'), tomcatConfig,
      overwrite: true);

  copy(join(projectRoot, 'docker/docker-compose.yaml'), versionDir,
      overwrite: true);

  /// copy in the war.
  final srcWar = find('*.war', workingDirectory: mvnTarget).firstLine!;
  final webappDir = join(versionDir, 'opt', 'tomcat', 'webapps');
  if (!exists(webappDir)) {
    createDir(webappDir);
  }

  final warPath = join(webappDir, 'pigation.${v.packageVersion}.war');
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
  final zip =
      join(projectRoot, 'releases', 'install_pigation-${v.packageVersion}.zip');

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
  /// -T 1C runs mvn with multiple threads - 1 per core.
  'mvn -T 1C -DskipTests install -U'.start(workingDirectory: pathToJavaProject);
}

void buildArmExes() {
  final pathToBin = DartProject.self.pathToBinDir;
  const pathToDockerBin = '/IrrigationForPi/build_tools/bin';
  final container = Container.create(Image.fromName('bsutton/pigation:latest'));
  final id = container.containerid;
  'docker cp $id:$pathToDockerBin/pig_build.exe $pathToBin/pig_build'.run;
  'docker cp $id:$pathToDockerBin/pig_install.exe $pathToBin/pig_install'.run;
  'docker cp $id:$pathToDockerBin/pig_reconfigure.exe $pathToBin/pig_reconfigure'
      .run;
  'docker cp $id:$pathToDockerBin/pig_start.exe $pathToBin/pig_start'.run;
  'docker cp $id:$pathToDockerBin/pig_stop.exe $pathToBin/pig_stop'.run;
}
