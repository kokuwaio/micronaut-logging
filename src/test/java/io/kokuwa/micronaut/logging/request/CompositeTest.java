package io.kokuwa.micronaut.logging.request;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.AbstractTest;
import io.micronaut.test.annotation.MicronautTest;

/**
 * Test for MDC and request filter combined.
 *
 * @author Stephan Schnabel
 */
@DisplayName("request-composite")
@MicronautTest(environments = "test-composite")
public class CompositeTest extends AbstractTest {

	@Inject
	TestClient client;

	@DisplayName("default level")
	@Test
	void defaultLogging() {
		client.assertLevel(Level.INFO, client.token("somebody"), null);
	}

	@DisplayName("level set by mdc")
	@Test
	void headerFromMdc() {
		client.assertLevel(Level.DEBUG, client.token("horst"), null);
	}

	@DisplayName("level set by header (overriding mdc)")
	@Test
	void headerFromHeader() {
		client.assertLevel(Level.TRACE, client.token("horst"), "TRACE");
	}
}
