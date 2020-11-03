#! /usr/bin/env dcli

import 'dart:io';

import 'package:dcli/dcli.dart';

/// Used during testing.
///
/// After running 'build' you can run this script to unzip
/// the install zip and run the installer.

void main() {
  if (!Script.current.isCompiled) {
    printerr(red(
        'You must use a compiled version of this script (because sudo will bugger things up'));
    exit(1);
  }

  if (!Shell.current.isPrivilegedUser) {
    printerr(red('you must run as root.'));
    exit(1);
  }

  var tmp = join(Script.current.pathToProjectRoot, 'tmp');
  // var target = join(Script.current.projectRoot, 'target');
  if (exists(tmp)) {
    deleteDir(tmp);
  }
  createDir(tmp);
  var zip = join(Script.current.pathToProjectRoot, 'install_auditor.zip');
  copy(zip, tmp);
  'unzip $zip'.start(workingDirectory: tmp);

  print('starting install');
  Script.current.pathToProjectRoot;

  /// run the install script we just unzipped into tmp.,
  './install'.start(workingDirectory: tmp, terminal: true);
}
