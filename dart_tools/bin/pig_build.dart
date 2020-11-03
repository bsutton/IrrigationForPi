#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:pub_release/pub_release.dart';

void main(List<String> args) {
  var parser = ArgParser();
  parser.addFlag('quick',
      abbr: 'q', defaultsTo: false, help: 'Skips rebuilding the war file.');

  var results = parser.parse(args);
  var quick = results['quick'] as bool;

  var pathToPubspec = findPubSpec();
  var pubspec = getPubSpec();

  print('Building the pigation installer, version ${pubspec.version}');
  var newVersion = askForVersion(pubspec.version);
  updateVersion(newVersion, pubspec, pathToPubspec);

  var script = Script.fromFile(Settings().pathToScript);
  var projectRoot = script.pathToProjectRoot;

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

  var versionDir = join(target, 'versions', newVersion.toString());
  createDir(versionDir, recursive: true);

  if (!quick) {
    print('building pigation');

    buildWar(projectRoot);
  }

  createZipImage(newVersion, versionDir, projectRoot, mvnTarget);

  var zip = createZip(target);

  showCompletedMessage(zip);
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
  print('Copy ${relative(zip)} to the target machine');
  print('run ${green('unzip install_pigation.zip')}');
  print("run ${green('sudo ./install')}");
}

String createZip(String target) {
  var zip = join(target, '..', 'install_pigation.zip');
  if (exists(zip)) {
    delete(zip);
  }

  print('dir: $pwd');

  'zip  -r $zip *'.start(workingDirectory: target);
  return zip;
}

void buildWar(String projectRoot) {
  print('Running git pull');
  'git pull'.run;

  //  -U forces an update of all snapshot jars
  'mvn -DskipTests install -U'.start(workingDirectory: join(projectRoot, '..'));
}
