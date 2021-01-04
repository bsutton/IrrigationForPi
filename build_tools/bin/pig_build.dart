#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:pigation/src/version/version.g.dart';
import 'package:pub_release/pub_release.dart';

var pathToPigation = join(HOME, 'pigation');
var pathToRepo = join(pathToPigation, 'IrrigationForPi');

String projectRoot;

/// You can manually run this when doing local testing.
///
void main(List<String> args) {
  var parser = ArgParser();

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
  var full = results['full'] as bool;
  var current = results['current'] as bool;
  var tools = results['tools'] as bool;

  if (!quick) {
    print(orange('Use --quick to avoid repeating the java build phase'));
  }

  projectRoot = Script.current.pathToProjectRoot;

  if (Script.current.isPubGlobalActivated) {
    projectRoot = pathToPigation;
  }

  if (full) {
    prepForBuild(tools);
  }
  var zip = build(quick: quick, current: current);

  showCompletedMessage(zip);
}

void prepForBuild(bool tools) {
  if (!exists(pathToRepo)) {
    print('Cloning project into $pathToRepo');

    createDir(pathToRepo, recursive: true);
    'git clone https://github.com/bsutton/IrrigationForPi.git'
        .start(workingDirectory: pathToPigation);
  } else {
    print('Pulling latest version of project into $pathToRepo');

    'git pull'.start(workingDirectory: pathToRepo);
  }

  if (tools) {
    print('Installing build tools');

    'apt install --no-install-recommends -y openjdk-8-jdk-headless maven'
        .start(privileged: true, runInShell: true);
  } else {
    print('Build tools will not be installed as --no-tools specified.');
  }
}

String build({bool quick, bool current}) {
  var pathToPubspec = findPubSpec();
  var pubspec = getPubSpec();

  final currentVersion = pubspec.version;
  var selectedVersion = pubspec.version;

  if (current) {
    print('Building the pigation installer, using version ${currentVersion}');
  } else {
    print('Building the pigation installer, current version ${currentVersion}');
    selectedVersion = askForVersion(pubspec.version);
    updateVersion(selectedVersion, pubspec, pathToPubspec);
  }

  /// clean the dart_tool target directory
  var target = join(projectRoot, 'target');
  if (exists(target)) {
    deleteDir(target, recursive: true);
  }

  /// clean the maven target directory unless we are running with quick
  var mvnTarget = join(join(projectRoot, '..', 'target'));
  if (!quick && exists(mvnTarget)) {
    deleteDir(mvnTarget, recursive: true);
  }

  var versionDir = join(target,  selectedVersion.toString());
  createDir(versionDir, recursive: true);

  if (!quick) {
    print('building pigation');

    buildWar(projectRoot);
  } else {
    print('Java build will be skipped as --quick specified');
  }

  createZipImage(selectedVersion, versionDir, projectRoot, mvnTarget);

  var zip = createZip(target);
  return zip;
}

void createZipImage(
    Version version, String versionDir, String projectRoot, String mvnTarget) {
  var include = join(versionDir, 'opt', 'nginx', 'include');
  if (!exists(include)) {
    createDir(include, recursive: true);
  }

  /// we are targeting arm so we have to do the compilation on the arm platform.
  // copyTree(join(projectRoot, 'config/nginx/include'), include, overwrite: true);

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
  var srcWar = find('*.war', root: mvnTarget, recursive: true).firstLine;
  var webappDir = join(versionDir, 'opt', 'tomcat', 'webapps');
  if (!exists(webappDir)) {
    createDir(webappDir);
  }
  copy(srcWar, join(webappDir, 'pigation.${version.toString()}.war'));
}

void showCompletedMessage(String zip) {
  print(orange('*' * 80));
  print('Build complete.');
  print('');

  print('Copy ${truepath(zip)} to the target machine and then run');
  print('run ${green('pig_install')}');
}

String createZip(String target) {
  var zip =
      join(projectRoot, 'releases', 'install_pigation-$packageVersion.zip');

  if (!exists(dirname(zip))) {
    createDir(dirname(zip), recursive: true);
  }
  if (exists(zip)) {
    delete(zip);
  }

  'zip  -r $zip *'.start(workingDirectory: target);
  return zip;
}

void buildWar(String projectRoot) {
  print('building java code');
  //  -U forces an update of all snapshot jars
  'mvn -DskipTests install -U'.start(workingDirectory: join(projectRoot, '..'));
}
