[![Build Status](https://app.bitrise.io/app/fa3f978235505e36/status.svg?token=-aLDA1C71XHB5RF5O2u3IA&branch=master)](https://app.bitrise.io/app/fa3f978235505e36)
[![Sonarcloud status](https://sonarcloud.io/api/project_badges/measure?project=bunpro-srs&metric=alert_status)](https://sonarcloud.io/dashboard?id=bunpro-srs)
# Bunpro Android App

This is the Android application for the [Bunpro](https://bunpro.jp/) japanese grammar learning service.

## Testing

To launch the instrumentation tests, you can type in your shell the following command to test everything:
"test_bunpro_login=*LOGIN* test_bunpro_password=*PASSWORD* ./gradlew connectedAndroidTest"
Or you can test only a specific class: "test_bunpro_login=*LOGIN* test_bunpro_password=*PASSWORD* ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=bunpro.jp.bunproapp.LoginActivityTest"

## Contributing

The project use the [Github issues](https://github.com/bunpro-srs/BunproAndroidApp/issues) to track down the bugs and log upcoming features.
Feel free to report the bugs you discover; we are also open to pull requests.

## License

This app is released under the MIT License. See LICENSE for details.
