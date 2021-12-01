# Set log level based on HTTP request header

With this features it is possible to set the log level while processing a request by adding the http header `x-log-level` with value `TRACE`. This log level is propagated to HTTP client requests.

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.http.level.enabled` | filter enabled? | `true`
`logger.http.level.path` | filter path | `/**`
`logger.http.level.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [ServerFilterPhase.FIRST.before()](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L34)
`logger.http.level.header` | name of HTTP header | `x-log-level`
`logger.http.level.propagation.enabled` | propagation enabled? | `true`
`logger.http.level.propagation.path` | propagation path | `/**`
`logger.http.level.propagation.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [Order.HIGHEST_PRECEDENCE](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java#L30)
`logger.http.level.propagation.header` | name of HTTP header | see `logger.http.level.header`

## Examples

Default configuration:

```yaml
logger:
  http:
    level:
      enabled: true
      order: -1000
      path: /**
      header: x-log-level
      propagation:
        enabled: true
        order: 2147483648
        path: /**
        header: ${logger.http.level.header}
```
