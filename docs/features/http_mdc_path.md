# Add HTTP path parts to MDC

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.http.path.enabled` | filter enabled? | `true`
`logger.http.path.path` | filter path | `/**`
`logger.http.path.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [ServerFilterPhase.FIRST.before()](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L34)
`logger.http.path.prefix` | prefix to MDC key | ``
`logger.http.path.patterns` | patterns with groups to add to MDC | `[]`

## Examples

Configuration for adding ids:

```yaml
logger:
  http:
    path:
      prefix: path.
      patterns:
        - \/gateway\/(?<gatewayId>[a-f0-9\-]{36})
        - \/gateway\/(?<gatewayId>[a-f0-9\-]{36})\/configuration\/(?<config>[a-z]+)
```
