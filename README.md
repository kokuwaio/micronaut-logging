# Micronaut Logging support

## Features

### Preconfigured Appender

Buildin appender:
 * console format
 * stackdriver format (with support for error reporting)

### Set log level based on MDC values

Confguration:
 * *enabled*: enable MDC filter (`true` is default)
 * *key*: MDC key, is optional (will use name instead, see example `user` below)
 * *level*: log level to use (`TRACE` is default)
 * *loggers*: whitelist of logger names, matches all loggers if empty
 * *values*: values for matching MDC key, matches all values if empty

Example for setting  different values for different values/logger:
```
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    gateway-debug:
      key: gateway
      level: DEBUG
      loggers:
      - io.kokuwa
      values:
      - 6a1bae7f-eb6c-4c81-af9d-dc15396584e2
      - fb3318f1-2c73-48e9-acd4-a2be3c9f9256
    gateway-trace:
      key: gateway
      level: TRACE
      loggers:
      - io.kokuwa
      - io.micronaut
      values:
      - 257802b2-22fe-4dcc-bb99-c1db2a47861f
```

Example for omiting level and key:
```
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    gateway:
      loggers:
      - io.kokuwa
      values:
      - 257802b2-22fe-4dcc-bb99-c1db2a47861f
      - 0a44738b-0c3a-4798-8210-2495485f10b2
```

Example for minimal configuration:
```
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    user: {}
```

### Set log level based on HTTP request header

Confguration:
 * *enabled*: enable HTTP request filter (`true` is default)
 * *order*: order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/master/core/src/main/java/io/micronaut/core/order/Ordered.java) (highest is default)
 * *pattern*: filter pattern (`/**` is default)
 * *header*: name of HTTP header (`x-log-level` is default)

Example with default configuration:
```
logger:
  request:
    header:
      enabled: true
      order: -2147483648
      pattern: /**
      header: x-log-level
```

### Add princial for request to MDC

Confguration:
 * *enabled*: enable HTTP principal filter (`true` is default)
 * *order*: order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/master/core/src/main/java/io/micronaut/core/order/Ordered.java) ([ServerFilterPhase.SECURITY.after()](https://github.com/micronaut-projects/micronaut-core/blob/v2.0.1/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L54) is default)
 * *pattern*: filter pattern (`/**` is default)
 * *key*: name of MDC header (`principal` is default)

Example with default configuration:
```
logger:
  request:
    principal:
      enabled: true
      order: 39250
      pattern: /**
      key: principal
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

 * configure mdc on refresh event
 * add stackdriver per configuration
 * add fluent per configuration
 * read **serviceName** and **serviceVersion** from yaml
