#! /usr/bin/env dcli



// ignore: prefer_relative_imports
import 'package:dcli/dcli.dart' hide equals;

String baseURl =
    'https://raw.githubusercontent.com/bsutton/dcli/master/test/src/functions/fetch_downloads';

void main(List<String> args) {
  withTempDir((testRoot) {
    withTempFile(
      (sampleAac) {
        fetch(
            url: '$baseURl/sample.aac',
            saveToPath: sampleAac,
            fetchProgress: FetchProgress.showBytes);
        delete(sampleAac);
      },
      create: false,
    );
  });
}
