# Micronaut Logging support

Enhanced logging for Micronaut using MDC or request header.

[![Maven Central](https://img.shields.io/maven-central/v/io.kokuwa.micronaut/micronaut-logging.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.kokuwa.micronaut/micronaut-logging)
[![license](https://img.shields.io/badge/License-EUPL%201.2-blue)](https://git.kokuwa.io/kokuwaio/micronaut-logging/src/branch/main/LICENSE)
[![prs](https://img.shields.io/gitea/pull-requests/open/kokuwaio/micronaut-logging?gitea_url=https%3A%2F%2Fgit.kokuwa.io)](https://git.kokuwa.io/kokuwaio/micronaut-logging/pulls)
[![issues](https://img.shields.io/gitea/issues/open/kokuwaio/micronaut-logging?gitea_url=https%3A%2F%2Fgit.kokuwa.io)](https://git.kokuwa.io/kokuwaio/micronaut-logging/issues)
[![build](https://ci.kokuwa.io/api/badges/kokuwaio/micronaut-logging/status.svg)](https://ci.kokuwa.io/repos/kokuwaio/micronaut-logging/)

Include in your `pom.xml`:

```xml
<dependency>
  <groupId>io.kokuwa.micronaut</groupId>
  <artifactId>micronaut-logging</artifactId>
  <version>${version.io.kokuwa.micronaut.logging}</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <!-- you can replace jsonp with jackson if you prefer jackson -->
  <groupId>io.micronaut.serde</groupId>
  <artifactId>micronaut-serde-jsonp</artifactId>
  <scope>runtime</scope>
</dependency>
```

## Features

* Version [3.x](https://github.com/kokuwaio/micronaut-logging/tree/3.x) is based on SLF4J 1.7 & Logback 1.2 & Micronaut 3.x
* Version [4.x](https://github.com/kokuwaio/micronaut-logging/tree/main) is based on SLF4J 2.0 & Logback 1.4 & Micronaut 4.x
* [set log level based on MDC values](docs/features/logback_mdc_level.md)
* [add default xml](docs/features/logback_default.md)
* [preconfigured appender for different environments](docs/features/logback_appender.md)
* [set log level based on HTTP request header](docs/features/http_log_level.md)
* [add HTTP path parts to MDC](docs/features/http_mdc_path.md)
* [add HTTP header to MDC](docs/features/http_mdc_header.md)
* [add authentication information from HTTP request to MDC](docs/features/http_mdc_authentication.md)

## Open Topics

* configure mdc on refresh event
* read **serviceName** and **serviceVersion** from yaml
* support auto select appender with custom `logback.xml`
