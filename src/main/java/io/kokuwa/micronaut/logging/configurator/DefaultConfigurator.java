package io.kokuwa.micronaut.logging.configurator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * Use logback-default.xml if no configuration is provided by user.
 *
 * @author Stephan Schnabel
 */
public class DefaultConfigurator extends ContextAwareBase implements Configurator {

	@Override
	public ExecutionStatus configure(LoggerContext loggerContext) {

		var base = DefaultConfigurator.class.getResource("/io/kokuwa/logback/logback-default.xml");
		if (base == null) {
			addError("Failed to find logback.xml from io.kokuwa:micronaut-logging");
			return ExecutionStatus.NEUTRAL;
		}

		try {
			addInfo("Use logback.xml from io.kokuwa:micronaut-logging");
			var configurator = new MicronautJoranConfigurator();
			configurator.setContext(loggerContext);
			configurator.doConfigure(base);
		} catch (JoranException e) {
			addError("Failed to load logback.xml from io.kokuwa:micronaut-logging", e);
			return ExecutionStatus.NEUTRAL;
		}

		loggerContext.getLogger("io.micronaut.logging.PropertiesLoggingLevelsConfigurer").setLevel(Level.WARN);
		return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
	}
}
