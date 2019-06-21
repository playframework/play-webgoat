# play-webgoat

A vulnerable Play application for attackers.

For example, this shows where unvalidated input from the client can be improperly trusted by the application and included in the response. There is also a sample cross-site scripting vulnerability.

## Fortify SCA

This `fortify` branch of the repository configures sbt to translate
the code for scanning by Fortify SCA.

The list of vulnerabilites Fortify finds is in [vulnerabilities.txt](https://github.com/playframework/play-webgoat/blob/fortify/vulnerabilities.txt).

For more information on Fortify SCA, see
[https://software.microfocus.com/en-us/products/static-code-analysis-sast/overview](https://software.microfocus.com/en-us/products/static-code-analysis-sast/overview).
Fortify's Scala support is documented at
[https://developer.lightbend.com/docs/fortify/latest/](https://developer.lightbend.com/docs/fortify/latest/).

## Running

```
sbt run
```

Then go to http://localhost:9000.

## Scala versions

Cross-building to Scala 2.11, 2.12 and 2.13 is supported.
