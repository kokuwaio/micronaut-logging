package io.kokuwa.micronaut.logging.request;

import ch.qos.logback.classic.Level;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.extern.slf4j.Slf4j;

/**
 * Contoller for testing {@link RequestLoggingHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
public class RequestLoggingController {

	@Get
	String run() {

		var level = Level.OFF;
		if (log.isTraceEnabled()) {
			level = Level.TRACE;
		} else if (log.isDebugEnabled()) {
			level = Level.DEBUG;
		} else if (log.isInfoEnabled()) {
			level = Level.INFO;
		} else if (log.isWarnEnabled()) {
			level = Level.WARN;
		} else if (log.isErrorEnabled()) {
			level = Level.ERROR;
		}

		return level.toString();
	}
}
