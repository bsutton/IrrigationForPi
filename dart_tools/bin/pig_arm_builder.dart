#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';

/// Installs docker, creates the docker group
/// and launches the qemu arm image.
void main() {
  // 'apt-get update && apt-get install -y --no-install-recommends qemu-user-static binfmt-support'.run;
  // 'update-binfmts --enable qemu-arm'.run;
  // 'update-binfmts --display qemu-arm'.run;

  print(orange('update'));
  'apt update'.start(privileged: true);
  print(orange('install docker'));
  'apt install -y docker'.start(privileged: true);
  print(orange('usermod'));
  'usermod -aG docker ${env['USER']}'.start(privileged: true);

  // var projectRoot = DartProject.current.pathToProjectRoot;
  // var armPath = createDir(join(projectRoot, 'arm'));

  //copy('/usr/bin/qemu-arm-static', armPath);

  // get the lates qemu image.
  /// the tag needs to be updated from time to time.
  // https://hub.docker.com/r/docker/binfmt/tags
  // 'docker pull docker/binfmt:a7996909642ee92942dcd6cff44b9b95f08dad64'.run;

  print(orange('docker run'));

  'docker run --rm --privileged docker/binfmt:820fdd95a9972a5308930a2bdfb8573dd4447ad3'
      .run;
}
