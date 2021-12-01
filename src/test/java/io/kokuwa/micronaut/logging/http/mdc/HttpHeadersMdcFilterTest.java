package io.kokuwa.micronaut.logging.http.mdc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.micronaut.logging.http.AbstractFilterTest;
import io.micronaut.context.annotation.Property;

/**
 * Test for {@link HttpHeadersMdcFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("http: mdc from headers")
public class HttpHeadersMdcFilterTest extends AbstractFilterTest {

	@DisplayName("noop: empty configuration")
	@Test
	void noopEmptyConfiguration() {
		assertContext(Map.of(), Map.of("foo", "bar"));
	}

	@DisplayName("noop: disabled")
	@Test
	@Property(name = "logger.http.headers.enabled", value = "false")
	@Property(name = "logger.http.headers.names", value = "foo")
	void noopDisabled() {
		assertContext(Map.of(), Map.of("foo", "bar"));
	}

	@DisplayName("mdc: mismatch")
	@Test
	@Property(name = "logger.http.headers.names", value = "foo")
	void mdcMismatch() {
		assertContext(Map.of(), Map.of("nope", "bar"));
	}

	@DisplayName("mdc: match without prefix")
	@Test
	@Property(name = "logger.http.headers.names", value = "foo")
	void mdcMatchWithoutPrefix() {
		assertContext(Map.of("foo", "bar"), Map.of("foo", "bar", "nope", "bar"));
	}

	@DisplayName("mdc: match with prefix")
	@Test
	@Property(name = "logger.http.headers.names", value = "foo")
	@Property(name = "logger.http.headers.prefix", value = "header.")
	void mdcMatchWithPrefix() {
		assertContext(Map.of("header.foo", "bar"), Map.of("foo", "bar", "nope", "bar"));
	}

	private void assertContext(Map<String, String> expectedMdcs, Map<String, String> headers) {
		assertEquals(expectedMdcs, get(headers).getContext());
	}
}
