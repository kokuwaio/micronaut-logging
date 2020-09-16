package io.kokuwa.micronaut.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.MDC;

import io.micronaut.test.annotation.MicronautTest;

/**
 * Base for tests regarding logging.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodName.class)
public abstract class AbstractTest {

	@BeforeEach
	@AfterEach
	void setUpMdc() {
		MDC.clear();
	}
}
