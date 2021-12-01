# Set log level based on MDC values

This can be used to change the log level based on MDC valus. E.g. change log levels for specific users/services etc.

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.mdc.enabled` | MDC enabled? | `true`
`logger.mdc.<key>` | MDC key to use |
`logger.mdc.<key>.key` | MDC key override, see complex example below for usage | `<key>`
`logger.mdc.<key>.level` | log level to use | `TRACE`
`logger.mdc.<key>.loggers` | passlist of logger names, matches all loggers if empty | `[]`
`logger.mdc.<key>.values` | values for matching MDC key, matches all values if empty | `[]`

## Examples

Minimal configuration that logs everything with `TRACE` if MDC `principal` is present:

```yaml
logger:
  levels:
    io.kokuwa: INFO
  mdc:
    principal: {}
```

Configuration that logs everything with `TRACE` for logger `io.kokuwa` if MDC `gateway` matches one value:

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

Complex example with setting different values for different values/logger:

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
