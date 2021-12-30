#! /usr/bin/env dcli

import 'package:dcli/dcli.dart';

import 'pig_install.dart';

final pathToPiImage = join(DartProject.self.pathToProjectRoot, 'pi_image');
final pathToKernel = join(pathToPiImage, 'qemu-rpi-kernel');

/// Update this based on the latest version available at
/// https://downloads.raspberrypi.org/raspios_lite_armhf/images
final imageReleaseDate = '2021-11-08';
final imageBuildDate = '2021-10-30';
final imageName = '$imageBuildDate-raspios-bullseye-armhf-lite';

/// Installer for qemu on linux from:
/// https://linuxconfig.org/how-to-run-the-raspberry-pi-os-in-a-virtual-machine-with-qemu-and-kvm
void main(List<String> args) async {
  // final parser = ArgParser();

  'apt install qemu-utils qemu-system-arm'.start(privileged: true);

  _buildImage();
  _buildKernal();
  _buildVM();
}

void _buildImage() {
  // Settings().setVerbose(enabled: true);
  final pathToZip = '$imageName.zip';

  if (!exists(pathToZip)) {
    fetch(
        url:
            // 'https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-2021-11-08/2021-10-30-raspios-bullseye-armhf-lite.zip'
            'https://downloads.raspberrypi.org/raspios_lite_armhf/images/raspios_lite_armhf-$imageReleaseDate/$imageName.zip',
        saveToPath: pathToZip,
        fetchProgress: FetchProgress.showBytes);
  }

  if (!exists(pathToPiImage)) {
    if (!exists(pathToPiImage)) {
      createDir(pathToPiImage);
    }
    unzip(pathToZip, pathToPiImage);
  }
}

void _buildKernal() {
  if (!exists(pathToKernel)) {
    createDir(pathToKernel);
    'git clone https://github.com/dhruvvyas90/qemu-rpi-kernel'
        .start(workingDirectory: pathToPiImage);
  }
}

final vmName = 'RaspPi';
final cpus = 8;
final memory = 4000; // in MB
final machine = 'versatilepb'; // raspi3

void _buildVM() {
  '''virt-install 
  --name $vmName  
  --arch armv6l 
  --machine $machine
  --cpu arm1176 
  --vcpus $cpus 
  --memory $memory 
  --import  
  --disk $imageName.img,format=raw,bus=virtio 
  --network bridge,source=virbr0,model=virtio  
  --video vga  
  --graphics spice 
  --boot 'dtb=qemu-rpi-kernel/versatile-pb-buster.dtb,kernel=qemu-rpi-kernel/kernel-qemu-4.19.50-buster,kernel_args=root=/dev/vda2 panic=1' 
  --events on_reboot=destroy'''
      .replaceAll('\n', ' ')
      .start(workingDirectory: pathToPiImage, privileged: true);
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
