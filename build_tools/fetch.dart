#! /usr/bin/env dcli

// ignore: prefer_relative_imports
import 'package:dcli/dcli.dart';

String baseURl =
    'https://raw.githubusercontent.com/bsutton/dcli/master/test/src/functions/fetch_downloads';

void main(List<String> args) async {
  await withTempDirAsync((testRoot) async {
    await withTempFileAsync (
      (sampleAac) async {
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
