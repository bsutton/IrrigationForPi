#! /usr/bin/env dcli

import 'dart:io';

import 'package:dcli/dcli.dart';
import 'package:dcli_scripts/dcli_scripts.dart';
import 'package:pigation/src/version/version.g.dart' as v;
import 'package:pub_release/pub_release.dart';

/// This script is used to compile the dart scripts to arm
/// ready for inclusing in the install zip file created
/// by pig_build.dart
///
/// Installs docker, creates the docker group
/// and launches the qemu arm image and
/// compiles each of the dart scripts
void main(List<String> args) {
  // 'apt-get update && apt-get install -y --no-install-recommends
  // qemu-user-static binfmt-support'.run;
  // 'update-binfmts --enable qemu-arm'.run;
  // 'update-binfmts --display qemu-arm'.run;

  final parser = ArgParser();

  print('pig_build ${v.packageVersion}');

  parser
    ..addFlag('debug', abbr: 'd', help: 'Enables debug output.')
    ..addFlag('fresh',
        abbr: 'f',
        defaultsTo: true,
        help: 'Forces a rebuild of the docker container from git clone.')
    ..addFlag('current',
        abbr: 'c',
        defaultsTo: true,
        help: 'If passed the current pubspec version no. is used.')
    ..addFlag('tools',
        abbr: 't',
        help: 'If passed then the java build tools are installed. '
            'Default to false.');

  ArgResults results;

  try {
    results = parser.parse(args);
  } on FormatException catch (e) {
    printerr(red(e.message));
    showUsage(parser);
    exit(1);
  }

  // final quick = results['quick'] as bool;
  Settings().setVerbose(enabled: results['debug'] as bool);
  // final full = results['full'] as bool;
  // final current = results['current'] as bool;
  final tools = results['tools'] as bool;
  final fresh = results['fresh'] as bool;
  final current = results['fresh'] as bool;

  if (tools) {
    print(orange('Update Apt'));
    'apt update'.start(privileged: true);
    print(orange('Install docker'));
    'apt install -y docker'.start(privileged: true);
    print(orange('usermod'));
    'usermod -aG docker ${env['USER']}'.start(privileged: true);

    // https://www.docker.com/blog/multi-platform-docker-builds/
    /// register the arm emulator for docker.
    'docker run --privileged --rm docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64'
        .run;
  } else {
    if (which('docker').notfound) {
      printerr(orange('Install docker and the build tools by passing in the '
          '--tools flag.'));
    }
  }

  if (!current) {
    final version = askForVersion(DartProject.self.pubSpec.version!);
    DartProject.self.pubSpec.version = version;
    DartProject.self.pubSpec.saveToFile(DartProject.self.pathToPubSpec);
  }

  // var projectRoot = DartProject.current.pathToProjectRoot;
  // var armPath = createDir(join(projectRoot, 'arm'));

  //copy('/usr/bin/qemu-arm-static', armPath);

  // get the lates qemu image.
  /// the tag needs to be updated from time to time.
  // https://hub.docker.com/r/docker/binfmt/tags
  // 'docker pull docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64'.run;

  print(orange('Build Dart tools in docker armv8 container'));

  // 'docker run --rm --privileged docker/binfmt:820fdd95a9972a5308930a2bdfb8573dd4447ad3'
  // .run;
  final pathToDockerFile = join(
      DartProject.self.pathToProjectRoot, 'docker', 'Dockerfile.arm.build');
  dockerPublish(
      pathToDockerFile: pathToDockerFile,
      push: false,
      repository: 'bsutton',
      fresh: fresh,
      buildArgs: ['--platform linux/arm64/v8']);
}

void showUsage(ArgParser parser) {
  print(parser.usage);
}
