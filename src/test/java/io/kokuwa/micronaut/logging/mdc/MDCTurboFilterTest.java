package io.kokuwa.micronaut.logging.mdc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.kokuwa.micronaut.logging.AbstractTest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Test for {@link MDCTurboFilterConfigurer}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("mdc based log levels")
@MicronautTest(environments = "test-mdc")
public class MDCTurboFilterTest extends AbstractTest {

	final Logger logA = LoggerFactory.getLogger("io.kokuwa.a");
	final Logger logB = LoggerFactory.getLogger("io.kokuwa.b");
	final Logger logC = LoggerFactory.getLogger("io.kokuwa.c");
	final Logger logOther = LoggerFactory.getLogger("org.example");

	@DisplayName("no key set")
	@Test
	void noKeySet() {
		assertFalse(logA.isDebugEnabled());
		assertFalse(logA.isTraceEnabled());
		assertFalse(logB.isDebugEnabled());
		assertFalse(logB.isTraceEnabled());
		assertFalse(logC.isDebugEnabled());
		assertFalse(logC.isTraceEnabled());
		assertFalse(logOther.isDebugEnabled());
		assertFalse(logOther.isTraceEnabled());
	}

	@DisplayName("match nothing")
	@Test
	void matchNothing() {
		MDC.put("key", "value-4");
		assertFalse(logA.isDebugEnabled());
		assertFalse(logA.isTraceEnabled());
		assertFalse(logB.isDebugEnabled());
		assertFalse(logB.isTraceEnabled());
		assertFalse(logC.isDebugEnabled());
		assertFalse(logC.isTraceEnabled());
		assertFalse(logOther.isDebugEnabled());
		assertFalse(logOther.isTraceEnabled());
	}

	@DisplayName("match root logger")
	@Test
	void matchRootLogger() {
		MDC.put("key", "value-3");
		assertTrue(logA.isDebugEnabled());
		assertTrue(logA.isTraceEnabled());
		assertTrue(logB.isDebugEnabled());
		assertTrue(logB.isTraceEnabled());
		assertTrue(logC.isDebugEnabled());
		assertTrue(logC.isTraceEnabled());
		assertFalse(logOther.isDebugEnabled());
		assertFalse(logOther.isTraceEnabled());
	}

	@DisplayName("match single filter")
	@Test
	void matchSingleFilter() {
		MDC.put("key", "value-1");
		assertTrue(logA.isDebugEnabled());
		assertFalse(logA.isTraceEnabled());
		assertTrue(logB.isDebugEnabled());
		assertFalse(logB.isTraceEnabled());
		assertFalse(logC.isDebugEnabled());
		assertFalse(logC.isTraceEnabled());
		assertFalse(logOther.isDebugEnabled());
		assertFalse(logOther.isTraceEnabled());
	}

	@DisplayName("match multiple filter")
	@Test
	void matchMultipleFilter() {
		MDC.put("key", "value-2");
		assertTrue(logA.isDebugEnabled());
		assertFalse(logA.isTraceEnabled());
		assertTrue(logB.isDebugEnabled());
		assertTrue(logB.isTraceEnabled());
		assertTrue(logC.isDebugEnabled());
		assertTrue(logC.isTraceEnabled());
		assertFalse(logOther.isDebugEnabled());
		assertFalse(logOther.isTraceEnabled());
	}

	@DisplayName("match simple config")
	@Test
	void matchSimpleConfig() {
		MDC.put("user", "foobar");
		assertTrue(logA.isDebugEnabled());
		assertTrue(logA.isTraceEnabled());
		assertTrue(logB.isDebugEnabled());
		assertTrue(logB.isTraceEnabled());
		assertTrue(logC.isDebugEnabled());
		assertTrue(logC.isTraceEnabled());
		assertTrue(logOther.isDebugEnabled());
		assertTrue(logOther.isTraceEnabled());
	}
}
