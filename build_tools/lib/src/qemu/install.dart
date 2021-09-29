import 'dart:io';

import 'package:args/command_runner.dart';
import 'package:dcli/dcli.dart';
import 'package:path/path.dart';
import 'package:pigation/src/qemu/paths.dart';

class InstallCommand extends Command<int> {
  InstallCommand() {
    argParser.addFlag('reset',
        abbr: 'r',
        defaultsTo: false,
        help: 'Reset the install and download files from scratch');
  }
  @override
  String get description => 'Install Qemu and a Raspberry PI image';

  @override
  String get name => 'install';

  static late final pathToQemuDownloads = join(HOME, 'qemu_for_pi');

  @override
  int run() {
    print('Installing Qemu into $pathToQemuDownloads');

    if (argResults!['reset'] as bool == true) {
      deleteDir(pathToQemuDownloads, recursive: true);
    }

    if (!Shell.current.isPrivilegedUser) {
      printerr(red('You must run this command as an Administrator'));
      exit(1);
    }
    install(pathToQemuDownloads: pathToQemuDownloads);
    return 0;
  }

  void install({required String pathToQemuDownloads}) {
    if (!exists(pathToQemuDownloads)) {
      createDir(pathToQemuDownloads, recursive: true);
    }
    final qemuSetupExePath = downloadQEMU(pathToQemuDownloads);
    final pathToPiImage = downloadPiKernel(pathToQemuDownloads);
    final dbtPath = downloadPiDbt(pathToQemuDownloads);

    if (which(qemuSystemArm).notfound) {
      runSetup(
          qemuSetupExePath: qemuSetupExePath,
          workingDirectory: pathToQemuDownloads);
    }

    init(pathToPiImage: pathToPiImage, workingDirectory: pathToQemuDownloads);
  }

  void init({required String pathToPiImage, required String workingDirectory}) {
    '$qemuSystemArmPath -kernel kernel-qemu-4.4.34-jessie -cpu arm1176 '
            '-m 256 -M versatilepb -no-reboot -serial stdio '
            '-append "root=/dev/sda2 panic=1 rootfstype=ext4 rw init=/bin/bash" '
            '-drive "file=$pathToPiImage,index=0,media=disk,format=raw" '
            '-redir tcp:2222::22'
        .start(workingDirectory: workingDirectory);

    final pathToRules = '/etc/udev/rules.d/90-qemu.rules';

    final rules_content = '''
KERNEL=="sda", SYM  LINK+="mmcblk0" KERNEL=="sda?", SYMLINK+="mmcblk0p%n" KERNEL=="sda2", SYMLINK+="root"
      ''';

    withTempFile((rules) {
      rules.write(rules_content);
      'scp rules localhost:2222:$pathToRules'.run;
    });
  }

  String downloadQEMU(String installDir) {
    final pathToQemuSetupExe = join(installDir, 'qemu-w64-setup.exe');

    if (!exists(pathToQemuSetupExe)) {
      print(blue('Downloading Qemu'));
      fetch(
          url: 'https://qemu.weilnetz.de/w64/qemu-w64-setup-20210825.exe',
          saveToPath: pathToQemuSetupExe,
          fetchProgress: FetchProgress.showBytes);
    }
    return pathToQemuSetupExe;
  }

  String downloadPiKernel(String installDir) {
    final pathToPiImage = join(installDir, 'kernel-qemu-5.4.51-buster');

    if (!exists(pathToPiImage)) {
      print(blue('Downloading Pi Kernel'));
      fetch(
          url:
              'https://raw.githubusercontent.com/dhruvvyas90/qemu-rpi-kernel/master/kernel-qemu-5.4.51-buster',
          saveToPath: pathToPiImage,
          fetchProgress: FetchProgress.showBytes);
    }
    return pathToPiImage;
  }

  String downloadPiDbt(String installDir) {
    final pathToPiDtb = join(installDir, 'versatile-pb-buster-5.4.51.dtb');

    if (!exists(pathToPiDtb)) {
      print(blue('Downloading PI Dtb'));
      fetch(
          url:
              'https://raw.githubusercontent.com/dhruvvyas90/qemu-rpi-kernel/master/versatile-pb-buster-5.4.51.dtb',
          saveToPath: pathToPiDtb,
          fetchProgress: FetchProgress.showBytes);
    }
    return pathToPiDtb;
  }

  void runSetup(
      {required String qemuSetupExePath, required String workingDirectory}) {
    qemuSetupExePath.start(workingDirectory: workingDirectory);
  }

  bool qemuInstalled() => qemuSystemArmPath != null;
}
