package io.kokuwa.micronaut.logging.request;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.AbstractTest;

/**
 * Test for {@link HeaderLoggingHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("request-header")
public class RequestHeaderTest extends AbstractTest {

	@Inject
	TestClient client;

	@DisplayName("header missing")
	@Test
	void headerMissing() {
		client.assertLevel(Level.INFO, null, null);
	}

	@DisplayName("header invalid, use DEBUG as default from logback")
	@Test
	void headerInvalid() {
		client.assertLevel(Level.DEBUG, null, "TRCE");
	}

	@DisplayName("level trace (below default)")
	@Test
	void headerLevelTrace() {
		client.assertLevel(Level.TRACE, null, "TRACE");
	}

	@DisplayName("level debug (below default)")
	@Test
	void headerLevelDebug() {
		client.assertLevel(Level.DEBUG, null, "DEBUG");
	}

	@DisplayName("level info (is default)")
	@Test
	void headerLevelInfo() {
		client.assertLevel(Level.INFO, null, "INFO");
	}

	@DisplayName("level warn (above default)")
	@Test
	void headerLevelWarn() {
		client.assertLevel(Level.INFO, null, "WARN");
	}
}
