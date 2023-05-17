package io.kokuwa.micronaut.logging.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.AbstractTest;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Test for {@link HttpServerFilter}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(rebuildContext = true)
public abstract class AbstractFilterTest extends AbstractTest {

	@Inject
	SignatureGeneratorConfiguration signature;
	@Inject
	EmbeddedServer embeddedServer;

	@DisplayName("0 - trigger rebuild of context")
	@Test
	void rebuild() {}

	// security

	public String token(String subject) {
		return token(subject, claims -> {});
	}

	public String token(String subject, Consumer<JWTClaimsSet.Builder> manipulator) {
		var claims = new JWTClaimsSet.Builder().subject(subject);
		manipulator.accept(claims);
		try {
			return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + signature.sign(claims.build()).serialize();
		} catch (JOSEException e) {
			fail(e);
			return null;
		}
	}

	// request

	public TestResponse get(String path, Map<String, String> headers) {

		var request = HttpRequest.GET(path);
		headers.forEach((name, value) -> request.header(name, value));
		var configuration = new DefaultHttpClientConfiguration();
		configuration.setLoggerName("io.kokuwa.TestClient");
		var response = HttpClient
				.create(embeddedServer.getURL(), configuration)
				.toBlocking().exchange(request, TestResponse.class);
		assertEquals(HttpStatus.OK, response.getStatus(), "status");
		assertTrue(response.getBody().isPresent(), "body");
		assertTrue(CollectionUtils.isEmpty(MDC.getCopyOfContextMap()), "mdc leaked: " + MDC.getCopyOfContextMap());

		return response.body();
	}

	@Secured({ SecurityRule.IS_ANONYMOUS, SecurityRule.IS_AUTHENTICATED })
	@Controller
	public static class TestController {

		private static final Logger log = LoggerFactory.getLogger(TestController.class);

		@Get("/{+path}")
		TestResponse run(@PathVariable String path) {

			var level = Level.OFF;
			if (log.isTraceEnabled()) {
				level = Level.TRACE;
			} else if (log.isDebugEnabled()) {
				level = Level.DEBUG;
			} else if (log.isInfoEnabled()) {
				level = Level.INFO;
			} else if (log.isWarnEnabled()) {
				level = Level.WARN;
			} else if (log.isErrorEnabled()) {
				level = Level.ERROR;
			}

			var mdc = MDC.getCopyOfContextMap();
			log.info("Found MDC: {}", mdc);

			return new TestResponse(path, level.toString(), mdc);
		}
	}

	@Serdeable
	public static class TestResponse {

		private final String path;
		private final String level;
		private final Map<String, String> context;

		@JsonCreator
		public TestResponse(
				@JsonProperty("path") String path,
				@JsonProperty("level") String level,
				@Nullable @JsonProperty("context") Map<String, String> context) {
			this.path = path;
			this.level = level;
			this.context = context == null ? Map.of() : context;
		}

		public String getPath() {
			return path;
		}

		public String getLevel() {
			return level;
		}

		public Map<String, String> getContext() {
			return context;
		}
	}
}
