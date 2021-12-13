# Micronaut Logging support

## Features

* [set log level based on MDC values](docs/features/logback_mdc_level.md)
* [add default xml](docs/features/logback_default.md)
* [preconfigured appender for different environments](docs/features/logback_appender.md)
* [set log level based on HTTP request header](docs/features/http_log_level.md)
* [add HTTP header to MDC](docs/features/http_mdc_header.md)
* [add authentication information from HTTP request to MDC](docs/features/http_mdc_authentication.md)

## Development

* [build](docs/build.md)

## Open Topics

* configure mdc on refresh event
* read **serviceName** and **serviceVersion** from yaml
* support auto select appender with custom `logback.xml`
