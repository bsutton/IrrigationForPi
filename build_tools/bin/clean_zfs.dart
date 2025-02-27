#! /usr/bin/env dcli

// ignore: unused_import
import 'package:dcli/dcli.dart';
import 'package:docker2/docker2.dart';

/// dcli script generated by:
/// dcli create %scriptname%
///
/// See
/// https://pub.dev/packages/dcli#-installing-tab-
///
/// For details on installing dcli.
///
void main(List<String> args) {
  final containers = Containers().containers();

  print('checking containers');
  for (final container in containers) {
    if (container.status == 'Removal In Progress') {
      print('deleting ${container.name}');
      final zpoolObject =
          "docker container inspect --format='{{.GraphDriver.Data.Dataset}}' "
                  '${container.containerid}'
              .toList()
              .first;
      container.delete();
      print('deleting zfs pool $zpoolObject');
      "zfs destroy -R '$zpoolObject'".run;
      "zfs destroy -R '$zpoolObject-init'".run;
      "zfs create '$zpoolObject'".run;
      "zfs create '$zpoolObject-init'".run;
    }
  }
}
