# Micronaut Logging support

## Features

 * Buildin appender:
   * console format
   * stackdriver format (with support for error reporting)
 * logback filter for log-levels per mdc

## Usage

MDC example:
```
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    gateway:
      level: DEBUG
      loggers:
      - io.kokuwa
      values:
      - 6a1bae7f-eb6c-4c81-af9d-dc15396584e2
      - fb3318f1-2c73-48e9-acd4-a2be3c9f9256
```

## Build & Release

### Dependency updates

Display dependency updates:
```
mvn versions:display-property-updates -U
```

Update dependencies:
```
mvn versions:update-properties
```

### Release locally

Run:
```
mvn release:prepare release:perform release:clean -B
```

## Open Topics

 * tests
 * configure mdc on refresh event
 * examples and documentation