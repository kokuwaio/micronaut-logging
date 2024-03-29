# Micronaut Logging support

[![Apache License 2.0](https://img.shields.io/github/license/kokuwaio/micronaut-logging)](https://github.com/kokuwaio/micronaut-logging/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.kokuwa.micronaut/micronaut-logging)](https://central.sonatype.com/namespace/io.kokuwa.micronaut)
[![Build](https://img.shields.io/github/actions/workflow/status/kokuwaio/micronaut-logging/build.yaml?branch=main)](https://github.com/kokuwaio/micronaut-logging/actions/workflows/build.yaml?query=branch%3Amain)

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
