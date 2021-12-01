# Add authentication information to MDC

This only applies to HTTP requests with successful security authentication.

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.http.authentication.enabled` | filter enabled? | `true`
`logger.http.authentication.path` | filter path | `/**`
`logger.http.authentication.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [ServerFilterPhase.SECURITY.after()](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L54)
`logger.http.authentication.prefix` | prefix to MDC key | ``
`logger.http.authentication.name` | MDC key of authentication name | `principal`
`logger.http.authentication.attributes` | authentication attributes to add to MDC, | `[]`

## Examples

Configuration for adding some jwt claims:

```yaml
logger:
  http:
    authentication:
      prefix: jwt.
      name: sub
      attributes:
        - aud
        - azp
```
