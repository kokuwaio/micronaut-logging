package io.kokuwa.micronaut.logging.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.micronaut.logging.AbstractTest;

/**
 * Test for {@link PrincipalHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("request-principal")
public class RequestPrincipalTest extends AbstractTest {

	@Inject
	TestClient client;

	@DisplayName("token missing")
	@Test
	void tokenMissing() {
		assertPrincipal(null, null);
	}

	@DisplayName("token invalid")
	@Test
	void tokenInvalid() {
		assertPrincipal(null, "meh");
	}

	@DisplayName("token valid")
	@Test
	void tokenValid() {
		assertPrincipal("meh", client.token("meh"));
	}

	private void assertPrincipal(String expectedPrincipal, String actualTokenValue) {
		assertEquals(expectedPrincipal, client.get(actualTokenValue, null).getPrincipal());
	}
}
