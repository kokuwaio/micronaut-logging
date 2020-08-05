package io.kokuwa.micronaut.logging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter for log levels based on mdc.
 *
 * @author Stephan Schnabel
 */
public class MDCTurboFilter extends TurboFilter {

	private final String key;
	private final Map<String, Boolean> cache = new HashMap<>();
	private final Set<String> loggers = new HashSet<>();
	private final Set<String> values = new HashSet<>();
	private Level level;

	public MDCTurboFilter(String name, String key, Context context) {
		this.key = key;
		this.level = Level.TRACE;
		this.setName(name);
		this.setContext(context);
	}

	public MDCTurboFilter setLoggers(Set<String> loggers) {
		this.cache.clear();
		this.loggers.clear();
		this.loggers.addAll(loggers);
		return this;
	}

	public MDCTurboFilter setValues(Set<String> values) {
		this.values.clear();
		this.values.addAll(values);
		return this;
	}

	public MDCTurboFilter setLevel(Level level) {
		this.level = level;
		return this;
	}

	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {

		if (logger == null || !isStarted() || values.isEmpty() || loggers.isEmpty()) {
			return FilterReply.NEUTRAL;
		}

		var value = MDC.get(key);
		if (value == null || !values.contains(value)) {
			return FilterReply.NEUTRAL;
		}

		var isLoggerIncluded = !cache.computeIfAbsent(logger.getName(), k -> loggers.stream().anyMatch(k::startsWith));
		if (isLoggerIncluded) {
			return FilterReply.NEUTRAL;
		}

		return level.isGreaterOrEqual(this.level) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
	}
}
