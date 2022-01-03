#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:pigation/src/version/version.g.dart' as v;

/// Installs docker, creates the docker group
/// and launches the qemu arm image.
void main(List<String> args) {
  // 'apt-get update && apt-get install -y --no-install-recommends
  // qemu-user-static binfmt-support'.run;
  // 'update-binfmts --enable qemu-arm'.run;
  // 'update-binfmts --display qemu-arm'.run;

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
            'Default to true.');

  final results = parser.parse(args);
  // final quick = results['quick'] as bool;
  // final debug = results['debug'] as bool;
  // final full = results['full'] as bool;
  // final current = results['current'] as bool;
  final tools = results['tools'] as bool;

  if (tools) {
    print(orange('Update Apt'));
    'apt update'.start(privileged: true);
    print(orange('Install docker'));
    'apt install -y docker'.start(privileged: true);
    print(orange('usermod'));
    'usermod -aG docker ${env['USER']}'.start(privileged: true);
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
  final pathToDockerFile =
      join(DartProject.self.pathToProjectRoot, 'docker', 'Dockerfile.build');
  final result =
      'docker  build --platform linux/arm64/v8 -f $pathToDockerFile -t pigation .'
          .start(nothrow: true);
  if (result.exitCode != 127) {
    printerr(result.toParagraph());
  }
}
