import 'package:dcli/dcli.dart';
import 'package:path/path.dart';
import 'package:settings_yaml/settings_yaml.dart';
import 'package:uuid/uuid.dart';

class PigationSettings {
  static const settingsFilename = 'settings.yaml';
  String? dbPassword;
  String? dbUsername;

  String? hostname;

  String? domain;

  String? tld;

  String? email;

  String? smtpHost;

  int? smtpPort;

  // ignore: prefer_constructors_over_static_methods
  static PigationSettings load() {
    if (!exists(dirname(path))) {
      createDir(dirname(path));
    }
    final yaml = SettingsYaml.load(pathToSettings: path);

    final settings = PigationSettings()
      ..hostname = yaml['hostname'] as String?
      ..domain = yaml['domain'] as String?
      ..tld = yaml['tld'] as String?
      ..dbUsername = yaml['dbUsername'] as String?
      ..dbPassword = yaml['dbPassword'] as String?
      ..email = yaml['email'] as String?
      ..smtpHost = yaml['smtpHost'] as String?
      ..smtpPort = yaml['smtpPort'] as int?
      ..hostname ??= ''
      ..domain ??= ''
      ..tld ??= ''
      ..smtpHost ??= ''
      ..smtpPort ??= 25
      ..dbUsername ??= 'pigation'
      ..dbPassword ??= const Uuid().v4();

    return settings;
  }

  static String get path => join('/opt', 'pigation', settingsFilename);

  Future<void> save() async {
    final yaml = SettingsYaml.load(pathToSettings: path);
    yaml['hostname'] = hostname;
    yaml['domain'] = domain;
    yaml['tld'] = tld;

    yaml['dbUsername'] = dbUsername;
    yaml['dbPassword'] = dbPassword;

    yaml['email'] = email;

    yaml['smtpHost'] = smtpHost;
    yaml['smtpPort'] = smtpPort;

    await yaml.save();
  }
}
