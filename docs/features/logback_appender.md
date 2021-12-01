# Appender

## Available Appender

* console with jansi for developers
* gcp logging format (with support for error reporting)
* json

## AutoSelect appender

1. if `LOGBACK_APPENDER` is set this appender will be used
2. if GCP is detected gcp appender will be used
3. if Kubernetes is detected json appender will be used
4. console appender else

*IMPORTENT*: only works without custom `logback.xml`
