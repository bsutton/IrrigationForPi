import 'package:path/path.dart';

String get qemuSystemArm => 'qemu-system-arm';

String? get qemuSystemArmPath => join(vmQemuPath, qemuSystemArm);

String get vmQemuPath => r'C:\Program Files\qemu';
