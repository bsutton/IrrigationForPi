#! /usr/bin/env dcli

// ignore_for_file: file_names

import 'dart:io';

import 'package:dcli/dcli.dart';
import 'package:path/path.dart';
import 'package:pub_release/pub_release.dart';
import 'package:settings_yaml/settings_yaml.dart';

/// Pushes the java zip file created by bin/pig_build to the git hub repo.
/// This allows pig_install to automatically down load the java zip file as
/// part of the install process.
///
///

void main(List<String> args) {
  const version = '1.0.12';
  // args[0];

  final project = DartProject.self;

  final pathToSettings = join(
      project.pathToProjectRoot, 'tool', 'post_release_hook', 'settings.yaml');
  final settings = SettingsYaml.load(pathToSettings: pathToSettings);
  final username = settings['username'] as String;
  final apiToken = settings['apiToken'] as String;
  final owner = settings['owner'] as String;
  final repository = settings['repository'] as String;

  print('$username $owner $repository');

  final sgh = SimpleGitHub(
      username: username,
      apiToken: apiToken,
      owner: owner,
      repository: repository)
    ..auth();

  final tagName = '$version-${Platform.operatingSystem}';

  final release = sgh.getReleaseByTagName(tagName: tagName);

  if (release != null) {
    print('Found release $tagName');

    final zipPath = join(DartScript.self.pathToProjectRoot, 'releases',
        'install_pigation-$version.zip');

    print('zipPath: $zipPath');

    addAsset(sgh, release, assetPath: zipPath);
    print('Asset sent');
  } else {
    printerr(red('Release for $tagName was not found'));
  }

  sgh.dispose();
}
