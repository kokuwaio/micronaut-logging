# Add HTTP headers to MDC

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.http.headers.enabled` | filter enabled? | `true`
`logger.http.headers.path` | filter path | `/**`
`logger.http.headers.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [ServerFilterPhase.FIRST.before()](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L34)
`logger.http.headers.prefix` | prefix to MDC key | ``
`logger.http.headers.names` | http header names to add to MDC | `[]`

## Examples

Configuration for b3-propagation:

```yaml
logger:
  http:
    headers:
      prefix: header.
      names:
        - x-request-id
        - x-b3-traceId 
        - x-b3-parentspanid
        - x-b3-spanid
        - x-b3-sampled 
```
