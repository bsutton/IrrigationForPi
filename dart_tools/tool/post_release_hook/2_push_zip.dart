#! /usr/bin/env dcli

import 'dart:io';

import 'package:dcli/dcli.dart';
import 'package:pub_release/pub_release.dart';
import 'package:settings_yaml/settings_yaml.dart';

/// Pushes the java zip file created by bin/pig_build to the git hub repo.
/// This allows pig_install to automatically down load the java zip file as
/// part of the install process.
///
///

void main(List<String> args) {
  final version = '1.0.12';
  // args[0];

  var project = DartProject.current;

  var pathToSettings = join(
      project.pathToProjectRoot, 'tool', 'post_release_hook', 'settings.yaml');
  var settings = SettingsYaml.load(pathToSettings: pathToSettings);
  var username = settings['username'] as String;
  var apiToken = settings['apiToken'] as String;
  var owner = settings['owner'] as String;
  var repository = settings['repository'] as String;

  print('$username $owner $repository');

  var sgh = SimpleGitHub(
      username: username,
      apiToken: apiToken,
      owner: owner,
      repository: repository);

  sgh.auth();

  final tagName = '$version-${Platform.operatingSystem}';

  var release = waitForEx(sgh.getByTagName(tagName: tagName));

  if (release != null) {
    print('Found release $tagName');

    final zipPath = join(Script.current.pathToProjectRoot, 'releases',
        'install_pigation-$version.zip');

    print('zipPath: $zipPath');

    addAsset(sgh, release, assetPath: zipPath);
    print('Asset sent');
  } else {
    printerr(red('Release for $tagName was not found'));
  }

  sgh.dispose();
}
