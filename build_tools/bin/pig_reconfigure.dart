#! /usr/bin/env dcli

import 'dart:io';

import 'package:collection/collection.dart' show IterableExtension;
import 'package:dcli/dcli.dart';
import 'package:docker2/docker2.dart';
import 'package:pigation/src/environment.dart';
import 'package:pigation/src/pigation_settings.dart';

PigationSettings? settings;

/// Run this script to reconfigure the Pigation settings and re-create
/// the containers
///
void main(List<String> args) {
  final parser = ArgParser()
    ..addFlag('debug', abbr: 'd', help: 'Outputs verbose logging.');

  final results = parser.parse(args);
  final debug = results['debug'] as bool;
  Settings().setVerbose(enabled: debug);

  if (!Shell.current.isPrivilegedUser) {
    printerr(red('You must run reconfig as the root user'));
    exit(1);
  }

  settings = PigationSettings.load();

  settings!.hostname = ask(
    'hostname',
    defaultValue: settings!.hostname,
    // validator: AskMultiValidator([Ask.alphaNumeric, Ask.required]));
  );
  settings!.domain = ask(
    'domain',
    defaultValue: settings!.domain,
    // validator: AskMultiValidator([Ask.fqdn, Ask.required]));
  );
  settings!.tld = ask(
    'tld',
    defaultValue: settings!.tld,
    //  validator: AskMultiValidator([Ask.alphaNumeric, Ask.required]));
  );

  settings!.smtpHost = ask('SMTP Host',
      defaultValue: settings!.smtpHost, validator: Ask.required);

  settings!.smtpPort =
      int.tryParse(ask('SMTP Port', defaultValue: '${settings!.smtpPort}'));
  // validator: Ask.integer));

  settings!.save();
  setEnvironment(settings!);

  reconfigure(settings);
}

void reconfigure(PigationSettings? settings) {
  print(green('Reconfiguring Pigation'));

  final auditorDir = join('/', 'opt', 'auditor');
  if (!exists(auditorDir)) {
    createDir(auditorDir, recursive: true);
  }

  print(green('Stopping Pigation'));
  '$auditorDir/stop'.run;

  print(green('The containers will to be started to finalise configuration.'));
  // delete the old named containers
  deleteContainer(name: 'nginx-le');
  deleteContainer(name: 'mysql');
  deleteContainer(name: 'backup_service');
  deleteContainer(name: 'tomcat');

  /// recreate the containers
  'docker-compose up -d'.run;

  // as we are running as root the pub-cache/bin path won't be on the path.
  Env().appendToPATH(
      join('/home', Shell.current.loggedInUser, PubCache().cacheDir, 'bin'));

  print('nginx-le configure');
  'nginx-le config'.run;

  print('Settings saved to ${PigationSettings.path}');

  print('Install complete and Pigation is running.');
  print('');

  '$auditorDir/stop'.run;

  print('');
  print(green('Reconfiguration complete.'));
  print('');

  print("Run ${green('start/stop')} to start/stop the containers");
}

void deleteContainer({String? name}) {
  final target = Containers()
      .containers()
      .firstWhereOrNull((container) => container.name == name);
  if (target != null) {
    target
      ..stop()
      ..delete();
  }
}

String get user {
  var user = env['SUDO_USER'];
  user ??= env['USER'];
  return user!;
}

class NoLocalHost extends AskValidator {
  @override
  String validate(String line) {
    if (line.trim() == 'localhost') {
      throw AskValidatorException(
          "localhost won't work as MySql treats this as a socket connection "
          'rather than an IP connection. Use 127.0.0.1 instead');
    }
    return line;
  }
}
