# Micronaut Logging support

This branch is for Micronaut 2.x, for 3.x see wip branch [3.x](../../tree/3.x).

## Features

### Default logback.xml

If no `logback.xml` by user is provided a default [logback.xml](src/main/resources/io/kokuwa/logback/logback-default.xml) is loaded. Otherwise use custom [logback.xml](src/main/resources/io/kokuwa/logback/logback-example.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true" scan="false">
 <include resource="io/kokuwa/logback/base.xml" />
 <logger name="io.micronaut.logging.PropertiesLoggingLevelsConfigurer" levels="WARN" />
 <root level="INFO">
  <appender-ref ref="${LOGBACK_APPENDER:-CONSOLE}" />
 </root>
</configuration>
```

### Available Appender

* console with jansi for developers
* gcp logging format (with support for error reporting)
* json

### AutoSelect appender logback.xml

1. if `LOGBACK_APPENDER` is set this appender will be used
2. if GCP is detected gcp appender will be used
3. if Kubernetes is detected json appender will be used
4. console appender else

*IMPORTENT*: only works without custom `logback.xml`

### Set log level based on MDC values

Configuration:

* *enabled*: enable MDC filter (`true` is default)
* *key*: MDC key, is optional (will use name instead, see example `user` below)
* *level*: log level to use (`TRACE` is default)
* *loggers*: passlist of logger names, matches all loggers if empty
* *values*: values for matching MDC key, matches all values if empty

Example for setting  different values for different values/logger:

```yaml
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

```yaml
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

```yaml
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    user: {}
```

### Set log level based on HTTP request header

Configuration for server filter (prefixed with *logger.request.filter*):

* *enabled*: enable HTTP server filter (`true` is default)
* *order*: order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v2.5.13/core/src/main/java/io/micronaut/core/order/Ordered.java) (highest is default)
* *path*: filter path (`/**` is default)
* *header*: name of HTTP header (`x-log-level` is default)

Configuration for client filter for propagation (prefixed with *logger.request.propagation*):

* *enabled*: enable HTTP client filter (`true` is default)
* *order*: order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v2.5.13/core/src/main/java/io/micronaut/core/order/Ordered.java) (tracing is default)
* *path*: filter path (`/**` is default)
* *header*: name of HTTP header (server header is default)

Example with default configuration:

```yaml
logger:
  request:
    filter:
      enabled: true
      order: -2147483648
      path: /**
      header: x-log-level
    propagation:
      enabled: true
      order: 19000
      path: /**
      header: ${logger.request.header.header-name}
```

### Add principal for request to MDC

Configuration:

* *enabled*: enable HTTP principal filter (`true` is default)
* *order*: order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v2.5.13/core/src/main/java/io/micronaut/core/order/Ordered.java) ([ServerFilterPhase.SECURITY.after()](https://github.com/micronaut-projects/micronaut-core/blob/v2.5.13/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L54) is default)
* *path*: filter path (`/**` is default)
* *key*: name of MDC header (`principal` is default)

Example with default configuration:

```yaml
logger:
  request:
    principal:
      enabled: true
      order: 39250
      path: /**
      key: principal
```

## Build & Release

### Dependency updates

Display dependency updates:

```sh
mvn versions:display-property-updates -U
```

Update dependencies:

```sh
mvn versions:update-properties
```

### Release locally

Run:

```sh
mvn release:prepare release:perform release:clean -B -DreleaseProfiles=oss-release
```

## Open Topics

* configure mdc on refresh event
* read **serviceName** and **serviceVersion** from yaml
* support auto select appender with custom `logback.xml`
