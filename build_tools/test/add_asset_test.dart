#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:pub_release/pub_release.dart';
import 'package:settings_yaml/settings_yaml.dart';

void main() {
  var project = DartProject.current;

  var pathToSettings = join(
      project.pathToProjectRoot, 'tool', 'post_release_hook', 'settings.yaml');
  var settings = SettingsYaml.load(pathToSettings: pathToSettings);
  var username = settings['username'] as String;
  var apiToken = settings['apiToken'] as String;
  var owner = settings['owner'] as String;
  var repository = settings['repository'] as String;

  final sgh = SimpleGitHub(
      username: username,
      apiToken: apiToken,
      owner: owner,
      repository: repository);

  sgh.auth();

  final release = waitForEx(sgh.getByTagName(tagName: 'latest-linux'));

  print(pwd);
  addAsset(sgh, release, assetPath: join('bin', 'pig_build'));

  sgh.dispose();
}
