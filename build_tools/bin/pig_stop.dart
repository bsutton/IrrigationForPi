#! /usr/bin/env dcli

import 'package:pigation/src/pigation_settings.dart';
import 'package:pigation/src/environment.dart';
import 'package:dcli/dcli.dart';

/// Use this command to stop auditor.
///

void main() {
  /// we don't actually require the environment vars but it
  /// stops docker-compose complaining that they are missing.
  var settings = PigationSettings.load();

  var path = findDockerCompose();

  setEnvironment(settings);
  'docker-compose stop'.start(workingDirectory: path);
}

String findDockerCompose() {
  var current = pwd;

  while (current != '/') {
    var compose = join(current, 'docker-compose.yaml');
    // could be in a parent directory or a directory call docker.
    var docker = join(current, 'docker', 'docker-compose.yaml');
    if (exists(compose)) {
      return dirname(compose);
    }
    if (exists(docker)) {
      return dirname(docker);
    }

    current = dirname(current);
  }

  return '/opt/pigation';
}
