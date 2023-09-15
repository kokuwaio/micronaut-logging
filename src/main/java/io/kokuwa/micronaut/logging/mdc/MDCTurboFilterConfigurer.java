package io.kokuwa.micronaut.logging.mdc;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.LogbackUtil;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.logging.LogLevel;
import io.micronaut.logging.LoggingSystem;

/**
 * Configure MDC filter.
 *
 * @author Stephan Schnabel
 */
@BootstrapContextCompatible
@Requires(beans = LogbackUtil.class)
@Requires(property = MDCTurboFilterConfigurer.PREFIX)
@Requires(property = MDCTurboFilterConfigurer.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Context
public class MDCTurboFilterConfigurer implements LoggingSystem {

	public static final String PREFIX = "logger.mdc";

	private static final Logger log = LoggerFactory.getLogger(MDCTurboFilterConfigurer.class);

	private final LogbackUtil logback;
	private final Environment environment;

	private Collection<String> mdcs = Set.of();
	private boolean initialized;

	public MDCTurboFilterConfigurer(LogbackUtil logback, Environment environment) {
		this.logback = logback;
		this.environment = environment;
		this.refresh();
	}

	@Override
	public void refresh() {

		mdcs = environment.getPropertyEntries(PREFIX);
		initialized = false;

		if (environment.getProperties("logger.levels").isEmpty()) {
			log.warn("MDCs are configured, but no levels are set. MDC may not work.");
		}
	}

	@Override
	public void setLogLevel(String name, LogLevel level) {
		if (!initialized) {
			configure();
			initialized = true;
		}
	}

	private void configure() {
		for (var name : mdcs) {

			var prefix = PREFIX + "." + name + ".";
			var key = environment.getProperty(prefix + "key", String.class, name);
			var loggers = environment.getProperty(prefix + "loggers", Argument.setOf(String.class)).orElseGet(Set::of);
			var values = environment.getProperty(prefix + "values", Argument.setOf(String.class)).orElseGet(Set::of);
			var level = Level.valueOf(environment.getProperty(prefix + "level", String.class, Level.TRACE.toString()));

			logback.getTurboFilter(MDCTurboFilter.class, name, MDCTurboFilter::new)
					.setKey(key)
					.setLevel(level)
					.setLoggers(loggers)
					.setValues(values)
					.start();

			log.info("Configured MDC filter {} for key {} with level {}.", name, key, level);
		}
	}
}
