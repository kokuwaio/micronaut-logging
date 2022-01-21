# Micronaut Logging support

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/kokuwaio/micronaut-logging.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/io.kokuwa.micronaut/micronaut-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.kokuwa.micronaut%22%20AND%20a:%22micronaut-logging%22)
[![CI](https://img.shields.io/github/workflow/status/kokuwaio/micronaut-logging/Snapshot)](https://github.com/kokuwaio/micronaut-logging/actions/workflows/snapshot.yaml)

Include in your `pom.xml`:

```xml
<dependency>
  <groupId>io.kokuwa.maven</groupId>
  <artifactId>helm-maven-plugin</artifactId>
  <version>6.0.0</version>
</dependency>
```

## Features

* [set log level based on MDC values](docs/features/logback_mdc_level.md)
* [add default xml](docs/features/logback_default.md)
* [preconfigured appender for different environments](docs/features/logback_appender.md)
* [set log level based on HTTP request header](docs/features/http_log_level.md)
* [add HTTP path parts to MDC](docs/features/http_mdc_path.md)
* [add HTTP header to MDC](docs/features/http_mdc_header.md)
* [add authentication information from HTTP request to MDC](docs/features/http_mdc_authentication.md)

## Development

* [build](docs/build.md)

## Open Topics

* configure mdc on refresh event
* read **serviceName** and **serviceVersion** from yaml
* support auto select appender with custom `logback.xml`
* add maven site with jacoco / dependency updates for snapshot build
