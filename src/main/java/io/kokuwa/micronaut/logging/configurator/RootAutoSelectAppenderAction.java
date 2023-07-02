package io.kokuwa.micronaut.logging.configurator;

import java.util.Optional;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import io.kokuwa.micronaut.logging.layout.GcpJsonLayout;
import io.kokuwa.micronaut.logging.layout.JsonLayout;
import io.micronaut.core.util.StringUtils;

/**
 * Auto select appender by environment.
 *
 * @author Stephan Schnabel
 */
public class RootAutoSelectAppenderAction extends Action {

	private static final boolean IS_KUBERNETES = StringUtils.isNotEmpty(System.getenv("KUBERNETES_SERVICE_HOST"));
	private static final boolean IS_GCP = StringUtils.isNotEmpty(System.getenv("GOOGLE_CLOUD_PROJECT"));

	private static final String APPENDER_CONSOLE = "CONSOLE";
	private static final String APPENDER_JSON = "JSON";
	private static final String APPENDER_GCP = "GCP";
	private static final String LOGBACK_APPENDER = "LOGBACK_APPENDER";
	private static final String LOGBACK_PATTERN = "LOGBACK_PATTERN";
	private static final String LOGBACK_PATTERN_DEFAULT = """
			%cyan(%d{HH:mm:ss.SSS}) \
			%gray(%-6.6thread) \
			%highlight(%-5level) \
			%magenta(%32logger{32}) \
			%mdc \
			%msg%n""";

	@Override
	public void begin(SaxEventInterpretationContext ic, String name, org.xml.sax.Attributes attributes) {

		var rootLogger = LoggerContext.class.cast(context).getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		var rootLoggerAppenders = rootLogger.iteratorForAppenders();
		if (rootLoggerAppenders.hasNext()) {
			addWarn("Skip because appender already found: " + rootLoggerAppenders.next().getName());
			return;
		}

		var envAppender = env(LOGBACK_APPENDER, null);
		if (envAppender != null) {
			setAppender(rootLogger, envAppender);
			return;
		}

		if (IS_KUBERNETES) {
			setAppender(rootLogger, APPENDER_JSON);
			return;
		}
		if (IS_GCP) {
			setAppender(rootLogger, APPENDER_GCP);
			return;
		}
		setAppender(rootLogger, APPENDER_CONSOLE);
	}

	@Override
	public void end(SaxEventInterpretationContext ic, String name) {}

	private void setAppender(Logger rootLogger, String appenderName) {
		addInfo("Use appender: " + appenderName);

		var layout = switch (appenderName) {
			case APPENDER_JSON -> json();
			case APPENDER_GCP -> gcp();
			case APPENDER_CONSOLE -> console();
			default -> {
				addError("Appender " + appenderName + " not found. Using console ...");
				yield console();
			}
		};
		layout.start();

		var encoder = new LayoutWrappingEncoder<ILoggingEvent>();
		encoder.setContext(context);
		encoder.setLayout(layout);
		encoder.start();

		var appender = new ConsoleAppender<ILoggingEvent>();
		appender.setContext(context);
		appender.setName(appenderName);
		appender.setEncoder(encoder);
		appender.start();

		rootLogger.addAppender(appender);
	}

	private Layout<ILoggingEvent> console() {
		var layout = new PatternLayout();
		layout.setContext(context);
		layout.setPattern(env(LOGBACK_PATTERN, LOGBACK_PATTERN_DEFAULT));
		return layout;
	}

	private Layout<ILoggingEvent> json() {
		var layout = new JsonLayout();
		layout.setContext(context);
		return layout;
	}

	private Layout<ILoggingEvent> gcp() {
		var layout = new GcpJsonLayout();
		layout.setContext(context);
		layout.setServiceName(env("SERVICE_NAME", null));
		layout.setServiceVersion(env("SERVICE_VERSION", null));
		return layout;
	}

	private String env(String name, String defaultValue) {
		var envValue = Optional.ofNullable(System.getenv(name)).map(String::trim).filter(StringUtils::isNotEmpty);
		var finalValue = envValue.orElse(defaultValue);
		if (envValue.isPresent()) {
			addInfo("Use provided config: " + name + "=" + finalValue);
		} else {
			addInfo("Use default config: " + name + "=" + finalValue);
		}
		return finalValue;
	}
}
