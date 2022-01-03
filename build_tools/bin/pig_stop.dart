#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:pigation/src/environment.dart';
import 'package:pigation/src/pigation_settings.dart';

/// Use this command to stop auditor.
///

void main() {
  /// we don't actually require the environment vars but it
  /// stops docker-compose complaining that they are missing.
  final settings = PigationSettings.load();

  final path = findDockerCompose();

  setEnvironment(settings);
  'docker-compose stop'.start(workingDirectory: path);
}

String findDockerCompose() {
  var current = pwd;

  while (current != '/') {
    final compose = join(current, 'docker-compose.yaml');
    // could be in a parent directory or a directory call docker.
    final docker = join(current, 'docker', 'docker-compose.yaml');
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
