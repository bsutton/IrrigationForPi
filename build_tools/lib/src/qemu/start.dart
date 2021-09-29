import 'package:args/command_runner.dart';
import 'package:dcli/src/util/string_as_process.dart';
import 'package:pigation/src/qemu/paths.dart';

class StartCommand extends Command<int> {
  @override
  String get description => 'Start Qemu with a Raspberry PI image';

  @override
  String get name => 'start';

  @override
  int run() {
    // start();

    return 0;
  }

// 2017-03-02-raspbian-jessie.img
  void start(
      {required String pathToPiImage, required String workingDirectory}) {
    '$qemuSystemArmPath -kernel kernel-qemu-4.4.34-jessie -cpu arm1176 '
            '-m 256 -M versatilepb -no-reboot -serial stdio '
            '-append "root=/dev/sda2 panic=1 rootfstype=ext4 rw" '
            '-drive "file=$pathToPiImage,index=0,media=disk,format=raw" '
            '-redir tcp:2222::22'
        .run;
  }
}
