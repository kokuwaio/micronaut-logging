package io.kokuwa.micronaut.logging.http.level;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.http.AbstractFilterTest;
import io.micronaut.context.annotation.Property;

/**
 * Test for {@link LogLevelServerFilter}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("http: set log level via http request")
public class LogLevelServerFilterTest extends AbstractFilterTest {

	@DisplayName("noop: disabled")
	@Test
	@Property(name = "logger.http.level.enabled", value = "false")
	void noopDisabled() {
		assertLevel(Level.INFO, "TRACE");
	}

	@DisplayName("noop: header missing")
	@Test
	void noopHeaderMissing() {
		assertLevel(Level.INFO, null);
	}

	@DisplayName("noop: header invalid, use DEBUG as default from logback")
	@Test
	void noopHeaderInvalid() {
		assertLevel(Level.DEBUG, "TRCE");
	}

	@DisplayName("level: trace (below default)")
	@Test
	void levelTrace() {
		assertLevel(Level.TRACE, "TRACE");
	}

	@DisplayName("level: debug (below default)")
	@Test
	void levelDebug() {
		assertLevel(Level.DEBUG, "DEBUG");
	}

	@DisplayName("level: info (is default)")
	@Test
	void levelInfo() {
		assertLevel(Level.INFO, "INFO");
	}

	@DisplayName("level: warn (above default)")
	@Test
	void levelWarn() {
		assertLevel(Level.INFO, "WARN");
	}

	@DisplayName("config: custom header name")
	@Test
	@Property(name = "logger.http.level.header", value = "FOO")
	void configHeaderWarn() {
		assertLevel(Level.TRACE, "FOO", "TRACE");
	}

	private void assertLevel(Level expectedLevel, String value) {
		assertLevel(expectedLevel, LogLevelServerFilter.DEFAULT_HEADER, value);
	}

	private void assertLevel(Level expectedLevel, String name, String value) {
		var headers = value == null ? Map.<String, String>of() : Map.of(name, value);
		assertEquals(expectedLevel.toString(), get("/level", headers).getLevel());
	}
}
