#! /usr/bin/env dcli

import 'package:pigation/src/pigation_settings.dart';
import 'package:pigation/src/environment.dart';
import 'package:dcli/dcli.dart';

/// Use this command to stop auditor.
///

void main() {
  /// we don't actually require the environment vars bit it
  /// stops docker-compose complaining that they are missing.
  var settings = PigationSettings.load();
  setEnvironment(settings);
  'docker-compose stop'.run;
}
