#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';
import 'package:path/path.dart';
import 'package:pub_release/pub_release.dart';
import 'package:settings_yaml/settings_yaml.dart';

void main() async {
  final project = DartProject.self;

  final pathToSettings = join(
      project.pathToProjectRoot, 'tool', 'post_release_hook', 'settings.yaml');
  final settings = SettingsYaml.load(pathToSettings: pathToSettings);
  final username = settings['username'] as String;
  final apiToken = settings['apiToken'] as String;
  final owner = settings['owner'] as String;
  final repository = settings['repository'] as String;

  final sgh = SimpleGitHub(
      username: username,
      apiToken: apiToken,
      owner: owner,
      repository: repository)
    ..auth();

  final release = await sgh.getReleaseByTagName(tagName: 'latest-linux');

  print(pwd);
  addAsset(sgh, release!, assetPath: join('bin', 'pig_build'));

  sgh.dispose();
}
