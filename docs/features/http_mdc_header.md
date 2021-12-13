# Add HTTP headers to MDC

## Properties

Property | Description | Default
-------- | ----------- | -------
`logger.http.header.enabled` | filter enabled? | `true`
`logger.http.header.path` | filter path | `/**`
`logger.http.header.order` | order for [Ordered](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/core/src/main/java/io/micronaut/core/order/Ordered.java) | [ServerFilterPhase.FIRST.before()](https://github.com/micronaut-projects/micronaut-core/blob/v3.2.0/http/src/main/java/io/micronaut/http/filter/ServerFilterPhase.java#L34)
`logger.http.header.prefix` | prefix to MDC key | ``
`logger.http.header.names` | http header names to add to MDC | `[]`

## Examples

Configuration for b3-propagation:

```yaml
logger:
  http:
    header:
      prefix: header.
      names:
        - x-request-id
        - x-b3-traceId 
        - x-b3-parentspanid
        - x-b3-spanid
        - x-b3-sampled 
```
