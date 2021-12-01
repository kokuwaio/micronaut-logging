package io.kokuwa.micronaut.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.MDC;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Base for tests regarding logging.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestClassOrder(ClassOrderer.DisplayName.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	@BeforeEach
	@AfterEach
	void setUpMdc() {
		MDC.clear();
	}
}
