package io.kokuwa.micronaut.logging;

import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.type.Argument;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;

/**
 * Configure mdc filter.
 *
 * @author Stephan Schnabel
 */
@BootstrapContextCompatible
@Context
@Requires(classes = LoggerContext.class)
@Requires(property = MDCTurboFilterConfigurer.LOGGER_MDCS_PROPERTY_PREFIX)
@Internal
public class MDCTurboFilterConfigurer implements ApplicationEventListener<RefreshEvent> {

	public static final String LOGGER_MDCS_PROPERTY_PREFIX = "logger.mdc";

	private static final Logger log = LoggerFactory.getLogger(MDCTurboFilterConfigurer.class);
	private final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	private final Environment environment;

	public MDCTurboFilterConfigurer(Environment environment) {
		this.environment = environment;
		configure();
	}

	@Override
	public void onApplicationEvent(RefreshEvent event) {
		if (event.getSource().keySet().stream().anyMatch(key -> key.startsWith(LOGGER_MDCS_PROPERTY_PREFIX))) {
			configure();
		}
	}

	private void configure() {
		for (var name : environment.getPropertyEntries(LOGGER_MDCS_PROPERTY_PREFIX)) {

			var prefix = LOGGER_MDCS_PROPERTY_PREFIX + "." + name + ".";
			var key = environment.getProperty(prefix + "key", String.class, name);
			var loggers = environment.getProperty(prefix + "loggers", Argument.setOf(String.class)).orElseGet(Set::of);
			var values = environment.getProperty(prefix + "values", Argument.setOf(String.class)).orElseGet(Set::of);
			var level = Level.valueOf(environment.getProperty(prefix + "level", String.class, Level.TRACE.toString()));

			getFilter(name, key).setLoggers(loggers).setValues(values).setLevel(level);

			log.info("Configured MDC filter {} for key {} with level {}.", name, key, level);
		}
	}

	private MDCTurboFilter getFilter(String name, String key) {

		// get filter

		var filterName = LOGGER_MDCS_PROPERTY_PREFIX + "." + key;
		var filterOptional = context.getTurboFilterList().stream()
				.filter(f -> Objects.equals(filterName, f.getName()))
				.filter(MDCTurboFilter.class::isInstance)
				.map(MDCTurboFilter.class::cast)
				.findAny();
		if (filterOptional.isPresent()) {
			return filterOptional.get();
		}

		// add filter

		var filter = new MDCTurboFilter(name, key, context);
		filter.start();
		context.addTurboFilter(filter);
		return filter;
	}
}
