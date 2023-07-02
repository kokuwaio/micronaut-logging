package io.kokuwa.micronaut.logging.http.mdc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.micronaut.logging.http.AbstractFilterTest;
import io.micronaut.context.annotation.Property;

/**
 * Test for {@link PathMdcFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("http: mdc from path")
public class PathMdcFilterTest extends AbstractFilterTest {

	@DisplayName("noop: empty configuration")
	@Test
	void noopEmptyConfiguration() {
		assertContext(Map.of(), "/foo/bar");
	}

	@DisplayName("noop: disabled")
	@Test
	@Property(name = "logger.http.path.enabled", value = "false")
	@Property(name = "logger.http.path.patterns", value = "\\/foo\\/(?<foo>[0-9]+)")
	void noopDisabled() {
		assertContext(Map.of(), "/foo/123");
	}

	@DisplayName("noop: misconfigured")
	@Test
	@Property(name = "logger.http.path.patterns", value = "\\A{")
	void noopMisconfigured() {
		assertContext(Map.of(), "/foo/123");
	}

	@DisplayName("noop: no group")
	@Test
	@Property(name = "logger.http.path.patterns", value = "\\/foo/[0-9]+")
	void noopGroups() {
		assertContext(Map.of(), "/foo/123");
	}

	@DisplayName("mdc: mismatch")
	@Test
	@Property(name = "logger.http.path.patterns", value = "\\/foo\\/(?<foo>[0-9]+)")
	void mdcMismatch() {
		assertContext(Map.of(), "/nope");
		assertContext(Map.of(), "/foo/abc");
	}

	@DisplayName("mdc: match with single group")
	@Test
	@Property(name = "logger.http.path.patterns", value = "\\/foo\\/(?<foo>[0-9]+)")
	void mdcMatchWithSingleGroup() {
		assertContext(Map.of("foo", "123"), "/foo/123");
	}

	@DisplayName("mdc: match with single group and prefix")
	@Test
	@Property(name = "logger.http.path.names", value = "foo")
	@Property(name = "logger.http.path.patterns", value = "\\/foo\\/(?<foo>[0-9]+)")
	@Property(name = "logger.http.path.prefix", value = "path.")
	void mdcMatchWithSingleGroupAndPrefix() {
		assertContext(Map.of("path.foo", "123"), "/foo/123");
	}

	@DisplayName("mdc: match with single group and misconfigured")
	@Test
	@Property(name = "logger.http.path.names", value = "foo")
	@Property(name = "logger.http.path.patterns", value = "\\/foo\\/(?<foo>[0-9]+),\\A{")
	@Property(name = "logger.http.path.prefix", value = "path.")
	void mdcMatchWithSingleGroupAndMisconfigured() {
		assertContext(Map.of("path.foo", "123"), "/foo/123");
	}

	@DisplayName("mdc: match with multiple group")
	@Test
	@Property(name = "logger.http.path.patterns", value = "/foo/(?<foo>[0-9]+)/bar/(?<bar>[0-9]+)")
	void mdcMatchWithmultipleGroup() {
		assertContext(Map.of("foo", "123", "bar", "456"), "/foo/123/bar/456");
	}

	@DisplayName("mdc: test for documentation example")
	@Test
	@Property(name = "logger.http.path.patterns", value = """
			\\/gateway\\/(?<gatewayId>[a-f0-9\\-]{36}),\
			\\/gateway\\/(?<gatewayId>[a-f0-9\\-]{36})\\/configuration\\/(?<config>[a-z]+)""")
	void mdcMatchExample() {
		var uuid = UUID.randomUUID().toString();
		assertContext(Map.of("gatewayId", uuid), "/gateway/" + uuid);
		assertContext(Map.of("gatewayId", uuid, "config", "abc"), "/gateway/" + uuid + "/configuration/abc");
	}

	private void assertContext(Map<String, String> expectedMdcs, String path) {
		assertEquals(expectedMdcs, get(path, Map.of()).getContext());
	}
}
