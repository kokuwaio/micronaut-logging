package io.kokuwa.micronaut.logging.request;

import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for testing {@link HeaderLoggingServerHttpFilter} and {@link PrincipalHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@Secured({ SecurityRule.IS_ANONYMOUS, SecurityRule.IS_AUTHENTICATED })
@Controller
@Slf4j
public class TestController {

	@Get("/")
	TestResponse run() {

		var principal = MDC.get(PrincipalHttpFilter.DEFAULT_KEY);
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

		return new TestResponse(level.toString(), principal);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TestResponse {
		private String level;
		private String principal;
	}
}
