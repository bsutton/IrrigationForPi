import 'package:pigation/src/pigation_settings.dart';
import 'package:test/test.dart';

void main() {
  test('pigation settings ...', () async {
    PigationSettings.load().save();
  });
}
