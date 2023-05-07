package io.kokuwa.micronaut.logging;

@io.micronaut.test.extensions.junit5.annotation.MicronautTest
public class LoggingTest {

	@org.junit.jupiter.api.Test
	void log() {
		var log = org.slf4j.LoggerFactory.getLogger(LoggingTest.class);
		log.trace("test-output-marker");
		log.debug("test-output-marker");
		log.info("test-output-marker");
	}
}
