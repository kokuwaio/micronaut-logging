package io.kokuwa.micronaut.logging.http.mdc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.micronaut.logging.http.AbstractFilterTest;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpHeaders;

/**
 * Test for {@link AuthenticationMdcFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("http: mdc from authentication")
public class AuthenticationMdcFilterTest extends AbstractFilterTest {

	@DisplayName("noop: disabled")
	@Test
	@Property(name = "logger.http.authentication.enabled", value = "false")
	void noopDisabled() {
		assertEquals(Map.of(), getContext(true));
	}

	@DisplayName("noop: token missing")
	@Test
	void noopTokenMissing() {
		assertEquals(Map.of(), getContext(false));
	}

	@DisplayName("mdc: default config")
	@Test
	void mdcWithDefault() {
		assertEquals(Map.of("principal", "mySubject"), getContext(true));
	}

	@DisplayName("mdc: with name")
	@Test
	@Property(name = "logger.http.authentication.name", value = "sub")
	void mdcWithName() {
		assertEquals(Map.of("sub", "mySubject"), getContext(true));
	}

	@DisplayName("mdc: with attribute keys")
	@Test
	@Property(name = "logger.http.authentication.attributes", value = "azp,aud")
	void mdcWithAttributes() {
		assertEquals(Map.of("principal", "mySubject", "aud", "[a, b]", "azp", "myAzp"), getContext(true));
	}

	@DisplayName("mdc: with prefix")
	@Test
	@Property(name = "logger.http.authentication.name", value = "sub")
	@Property(name = "logger.http.authentication.attributes", value = "azp")
	@Property(name = "logger.http.authentication.prefix", value = "auth.")
	void mdcWithPrefix() {
		assertEquals(Map.of("auth.sub", "mySubject", "auth.azp", "myAzp"), getContext(true));
	}

	private Map<String, String> getContext(boolean token) {
		return get("/security", token
				? Map.of(HttpHeaders.AUTHORIZATION, token("mySubject", claims -> claims
						.issuer("nope")
						.claim("azp", "myAzp")
						.audience(List.of("a", "b"))))
				: Map.of()).getContext();
	}
}
