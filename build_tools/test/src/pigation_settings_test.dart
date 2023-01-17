import 'package:pigation/src/pigation_settings.dart';
import 'package:test/test.dart';

void main() async {
  test('pigation settings ...', () async {
    await PigationSettings.load().save();
  });
}
