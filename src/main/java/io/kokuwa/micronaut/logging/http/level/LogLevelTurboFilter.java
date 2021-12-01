package io.kokuwa.micronaut.logging.http.level;

import org.slf4j.MDC;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Filter for log levels based on MDC.
 *
 * @author Stephan Schnabel
 */
public class LogLevelTurboFilter extends TurboFilter {

	@Override
	public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {

		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}

		var value = MDC.get(LogLevelServerFilter.MDC_KEY);
		if (value == null) {
			return FilterReply.NEUTRAL;
		}

		return level.isGreaterOrEqual(Level.valueOf(value)) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
	}
}
