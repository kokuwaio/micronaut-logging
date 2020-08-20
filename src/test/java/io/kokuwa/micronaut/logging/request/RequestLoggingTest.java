package io.kokuwa.micronaut.logging.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.AbstractTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

/**
 * Test for {@link RequestLoggingHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("request")
public class RequestLoggingTest extends AbstractTest {

	@Inject
	@Client("/")
	HttpClient client;

	@DisplayName("header missing")
	@Test
	void headerMissing() {
		assertLevel(Level.INFO, null);
	}

	@DisplayName("header invalid, use DEBUG as default from logback")
	@Test
	void headerInvalid() {
		assertLevel(Level.DEBUG, "TRCE");
	}

	@DisplayName("level trace (below default)")
	@Test
	void headerLevelTrace() {
		assertLevel(Level.TRACE, "TRACE");
	}

	@DisplayName("level debug (below default)")
	@Test
	void headerLevelDebug() {
		assertLevel(Level.DEBUG, "DEBUG");
	}

	@DisplayName("level info (is default)")
	@Test
	void headerLevelInfo() {
		assertLevel(Level.INFO, "INFO");
	}

	@DisplayName("level warn (above default)")
	@Test
	void headerLevelWarn() {
		assertLevel(Level.INFO, "WARN");
	}

	private void assertLevel(Level expected, String header) {

		var request = HttpRequest.GET("/");
		if (header != null) {
			request.getHeaders().add(RequestLoggingHttpFilter.DEFAULT_HEADER, header);
		}

		var response = client.toBlocking().exchange(request, String.class);
		assertEquals(HttpStatus.OK, response.getStatus(), "status");
		assertTrue(response.getBody().isPresent(), "body");
		assertEquals(expected, Level.valueOf(response.body()), "level");
	}
}
