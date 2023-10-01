import 'package:dcli/dcli.dart';
import 'package:pub_release/pub_release.dart';
import 'package:test/test.dart';

void main() {
  test('using it', () {
    expect(Git(DartScript.self.pathToProjectRoot).usingGit, equals(true));
  });
}
