#! /usr/bin/env dcli

// ignore_for_file: file_names

import 'package:dcli/dcli.dart';
import 'package:settings_yaml/settings_yaml.dart';

void main(List<String> args) {
  final project = DartProject.self;

  final pathToSettings = join(
      project.pathToProjectRoot, 'tool', 'post_release_hook', 'settings.yaml');
  final settings = SettingsYaml.load(pathToSettings: pathToSettings);
  final username = settings['username'] as String?;
  final apiToken = settings['apiToken'] as String?;
  final owner = settings['owner'] as String?;
  final repository = settings['repository'] as String?;

  'github_release -u $username --apiToken $apiToken --owner $owner '
          '--repository $repository'
      .start(workingDirectory: DartScript.self.pathToProjectRoot);
}
