import 'package:dcli/dcli.dart';
import 'package:settings_yaml/settings_yaml.dart';
import 'package:uuid/uuid.dart';

class PigationSettings {
  static const SETTINGS_FILENAME = 'settings.yaml';
  String dbPassword;
  String dbUsername;

  String hostname;

  String domain;

  String tld;

  String email;

  String smtpHost;

  int smtpPort;

  static PigationSettings load() {
    if (!exists(dirname(path))) {
      createDir(dirname(path));
    }
    var yaml = SettingsYaml.load(pathToSettings: path);

    var settings = PigationSettings();

    settings.hostname = yaml['hostname'] as String;
    settings.domain = yaml['domain'] as String;
    settings.tld = yaml['tld'] as String;
    settings.dbUsername = yaml['dbUsername'] as String;
    settings.dbPassword = yaml['dbPassword'] as String;

    settings.email = yaml['email'] as String;

    settings.smtpHost = yaml['smtpHost'] as String;
    settings.smtpPort = yaml['smtpPort'] as int;

    settings.hostname ??= '';
    settings.domain ??= '';
    settings.tld ??= '';

    settings.smtpHost ??= '';
    settings.smtpPort ??= 25;

    settings.dbUsername ??= 'pigation';
    settings.dbPassword ??= Uuid().v4();

    return settings;
  }

  static String get path {
    return join('/opt', 'pigation', SETTINGS_FILENAME);
  }

  void save() {
    var yaml = SettingsYaml.load(pathToSettings: path);
    yaml['hostname'] = hostname;
    yaml['domain'] = domain;
    yaml['tld'] = tld;

    yaml['dbUsername'] = dbUsername;
    yaml['dbPassword'] = dbPassword;

    yaml['email'] = email;

    yaml['smtpHost'] = smtpHost;
    yaml['smtpPort'] = smtpPort;

    yaml.save();
  }
}
