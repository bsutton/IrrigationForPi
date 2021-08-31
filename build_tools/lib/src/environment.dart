import 'package:dcli/dcli.dart';

import 'pigation_settings.dart';

void setEnvironment(PigationSettings settings) {
  env['CERT_HOSTNAME'] = settings.hostname;
  env['CERT_DOMAIN'] = settings.domain;
  env['CERT_TLD'] = settings.tld;
  env['DB_PASSWORD'] = settings.dbPassword;
  env['DB_USERNAME'] = settings.dbUsername;
  env['EMAIL_ADDRESS'] = settings.email;

  env['SMTP_SERVER'] = settings.smtpHost;
  env['SMTP_SERVER_PORT'] = '${settings.smtpPort}';

  /// we set the next three env to suppress warnings on start
  /// we don't normally use these
  if (env['NAMECHEAP_API_KEY'] == null) {
    env['NAMECHEAP_API_KEY'] = '';
  }

  if (env['NAMECHEAP_API_USER'] == null) {
    env['NAMECHEAP_API_USER'] = '';
  }
  if (env['DB_EXTRA_ARGS'] == null) {
    env['DB_EXTRA_ARGS'] = '';
  }
}
