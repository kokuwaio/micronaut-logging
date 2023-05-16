package io.kokuwa.micronaut.logging;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

/**
 * Utility class for Logback operations.
 *
 * @author Stephan Schnabel
 */
@Requires(classes = LoggerContext.class)
@BootstrapContextCompatible
@Singleton
public class LogbackUtil {

	private final LoggerContext context;

	public LogbackUtil() {
		this.context = (LoggerContext) LoggerFactory.getILoggerFactory();
	}

	public <T extends TurboFilter> Optional<T> getTurboFilter(Class<T> type, String name) {
		return context.getTurboFilterList().stream()
				.filter(filter -> Objects.equals(name, filter.getName()))
				.filter(type::isInstance)
				.map(type::cast).findAny();
	}

	public <T extends TurboFilter> T getTurboFilter(Class<T> type, String name, Supplier<T> defaultFilter) {
		return getTurboFilter(type, name).orElseGet(() -> {
			var filter = defaultFilter.get();
			filter.setName(name);
			filter.setContext(context);
			context.addTurboFilter(filter);
			return filter;
		});
	}
}
