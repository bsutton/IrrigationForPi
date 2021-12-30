#! /usr/bin/env dcli

import 'dart:io';

import 'package:args/command_runner.dart';
import 'package:dcli/dcli.dart';
import 'package:pigation/src/qemu/install.dart';
import 'package:pigation/src/qemu/start.dart';

void main(List<String> args) async {
  // final parser = ArgParser();

  final runner = CommandRunner<int>('qemu_for_pi',
      'Installs and starts Qemu for doing Pigation buids for the raspberry PI on Windows')
    ..addCommand(InstallCommand())
    ..addCommand(StartCommand());

  final exitCode = await runner.run(args) ?? 0;
  exit(exitCode);

  //   printerr(red(e.message));
  //   showUsage(parser);
  //   exit(1);
}

// if (parsed.name == 'install') {
//   parsed.command.
// } else if (parsed.name == 'start') {
//   // start(pathToQemuInstall: pathToQemuInstall);
// } else {
//   printerr(red('Invalid command: ${parsed.name}'));
//   showUsage(parser);
//   exit(1);
// }

void showUsage(ArgParser parser) {
  print(
      'Installs a Raspberry Pi emulator on Windows which can be used to build Pigation');
  print(green('Usage:'));
  print('qemu_for_windows.dart install|start');
  print(parser.usage);
}
