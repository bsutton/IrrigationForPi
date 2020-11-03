#! /usr/bin/env dcli

import 'dart:io';

import 'package:pigation/src/pigation_settings.dart';
import 'package:pigation/src/environment.dart';
import 'package:dcli/dcli.dart';

/// Use this command to start auditor.
///
void main(List<String> args) {
  var parser = ArgParser();

  parser.addFlag('help',
      abbr: 'h', defaultsTo: false, help: 'Displays this help message..');

  parser.addFlag('debug',
      abbr: 'd', defaultsTo: false, help: 'Outputs verbose logging.');

  parser.addFlag('create',
      abbr: 'c', defaultsTo: false, help: 'Creates/Recreates the containers.');

  var results = parser.parse(args);
  var debug = results['debug'] as bool;
  Settings().setVerbose(enabled: debug);

  var help = results['help'] as bool;
  if (help) {
    print(parser.usage);
    exit(1);
  }

  var settings = PigationSettings.load();
  setEnvironment(settings);

  var path = findDockerCompose();

  if (path == null) {
    printerr(red(
        'Cannot find the docker-compose.yaml. It should be in the current directory or a parent directory'));
    exit(1);
  }

  print(orange('Starting ${truepath(path)}'));

  'docker-compose stop'.start(workingDirectory: path);
  'docker-compose up --no-recreate -d'.start(workingDirectory: path);
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
  return null;
}
