# play-webgoat

A vulnerable Play application for attackers.

This application stays clear of the Twirl template engine for the most part, and shows where unvalidated input from the client can be improperly trusted by the application and included in the response.

## Running

### Using sbt

```bash
sbt run
```

### Using Gradle

```bash
./gradlew runPlayBinary
```

Then go to http://localhost:9000.

## Scala versions

Cross-building to Scala 2.11 and 2.12 is supported.
