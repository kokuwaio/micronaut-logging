package io.kokuwa.micronaut.logging.configurator;

import java.util.Map;

import org.xml.sax.Attributes;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.spi.InterpretationContext;
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

	@Override
	public void begin(InterpretationContext ic, String name, Attributes attributes) {

		var rootLogger = LoggerContext.class.cast(context).getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		var rootLoggerAppenders = rootLogger.iteratorForAppenders();
		if (rootLoggerAppenders.hasNext()) {
			addWarn("Skip because appender already found: " + rootLoggerAppenders.next().getName());
			return;
		}

		var envAppender = System.getenv(LOGBACK_APPENDER);
		if (envAppender != null && setAppender(ic, rootLogger, envAppender)) {
			return;
		}

		if (IS_KUBERNETES && setAppender(ic, rootLogger, APPENDER_JSON)) {
			return;
		}
		if (IS_GCP && setAppender(ic, rootLogger, APPENDER_GCP)) {
			return;
		}
		setAppender(ic, rootLogger, APPENDER_CONSOLE);
	}

	@Override
	public void end(InterpretationContext ic, String name) {}

	private boolean setAppender(InterpretationContext ic, Logger rootLogger, String appenderName) {

		@SuppressWarnings("unchecked")
		var appenderBag = (Map<String, Appender<ILoggingEvent>>) ic.getObjectMap().get(ActionConst.APPENDER_BAG);
		var appender = appenderBag.get(appenderName);
		if (appender == null) {
			addError("Could not find an appender named [" + appenderName
					+ "]. Did you define it below instead of above in the configuration file?");
			return false;
		}

		addInfo("Use appender: " + appenderName);
		rootLogger.addAppender(appender);
		return true;
	}
}
