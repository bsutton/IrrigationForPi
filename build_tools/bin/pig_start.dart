#! /usr/bin/env dcli

import 'dart:io';

import 'package:args/args.dart';
import 'package:dcli/dcli.dart';
import 'package:path/path.dart';
import 'package:pigation/src/environment.dart';
import 'package:pigation/src/pigation_settings.dart';

/// Use this command to start pi-gation.
///
void main(List<String> args) {
  final parser = ArgParser()
    ..addFlag('help', abbr: 'h', help: 'Displays this help message..')
    ..addFlag('debug', abbr: 'd', help: 'Outputs verbose logging.')
    ..addFlag('create', abbr: 'c', help: 'Creates/Recreates the containers.');

  final results = parser.parse(args);
  final debug = results['debug'] as bool;
  Settings().setVerbose(enabled: debug);

  final help = results['help'] as bool;
  if (help) {
    print(parser.usage);
    exit(1);
  }

  final settings = PigationSettings.load();
  setEnvironment(settings);

  final path = findDockerCompose();

  print(orange('Starting ${truepath(path)}'));

  'docker-compose stop'.start(workingDirectory: path);
  'docker-compose up --no-recreate -d'.start(workingDirectory: path);
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
  printerr(
      red('Cannot find the docker-compose.yaml. It should be in the current '
          'directory or a parent directory'));
  exit(1);
}
