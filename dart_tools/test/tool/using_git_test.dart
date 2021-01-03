import 'package:dcli/dcli.dart' hide equals;
import 'package:test/test.dart';
import 'package:pub_release/pub_release.dart';

void main() {
  test('using it', () {
    expect(Git().usingGit(Script.current.pathToProjectRoot), equals(true));
  });
}


