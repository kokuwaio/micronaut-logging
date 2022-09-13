# Micronaut Logging support

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/kokuwaio/micronaut-logging.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/io.kokuwa.micronaut/micronaut-logging.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.kokuwa.micronaut%22%20AND%20a:%22micronaut-logging%22)
[![CI](https://img.shields.io/github/workflow/status/kokuwaio/micronaut-logging/CI?label=CI)](https://github.com/kokuwaio/micronaut-logging/actions/workflows/ci.yaml?label=CI)

Include in your `pom.xml`:

```xml
<dependency>
  <groupId>io.kokuwa.micronaut</groupId>
  <artifactId>micronaut-logging</artifactId>
  <version>${version.io.kokuwa.micronaut.logging}</version>
  <scope>runtime</scope>
</dependency>
```

## Features

* Version [3.x](https://github.com/kokuwaio/micronaut-logging/tree/3.x) is based on SLF4J 1.7 & Logback 1.2
* Version [4.x](https://github.com/kokuwaio/micronaut-logging/tree/main) is based on SLF4J 2.0 & Logback 1.4
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
