# Add default logback.xml

If no `logback.xml` by user is provided a default [logback.xml](../../src/main/resources/io/kokuwa/logback/logback-default.xml) is loaded. Otherwise use custom [logback.xml](../../src/main/resources/io/kokuwa/logback/logback-example.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true" scan="false">

 <include resource="io/kokuwa/logback/base.xml" />

 <logger name="io.micronaut.logging.PropertiesLoggingLevelsConfigurer" levels="WARN" />

 <root level="INFO">
  <appender-ref ref="${LOGBACK_APPENDER:-CONSOLE}" />
 </root>

</configuration>
```
