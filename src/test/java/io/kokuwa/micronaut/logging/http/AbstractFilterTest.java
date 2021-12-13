package io.kokuwa.micronaut.logging.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import com.nimbusds.jwt.JWTClaimsSet;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.AbstractTest;
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Test for {@link HttpServerFilter}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(rebuildContext = true)
public abstract class AbstractFilterTest extends AbstractTest {

	private static boolean INIT = false;

	@Inject
	SignatureGeneratorConfiguration signature;
	@Inject
	EmbeddedServer embeddedServer;

	@DisplayName("0 init")
	@Test
	@BeforeEach
	void refresh() {
		// https://github.com/micronaut-projects/micronaut-core/issues/5453#issuecomment-864594741
		if (INIT) {
			embeddedServer.refresh();
		} else {
			INIT = true;
		}
	}

	// security

	public String token(String subject) {
		return token(subject, claims -> {});
	}

	@SneakyThrows
	public String token(String subject, Consumer<JWTClaimsSet.Builder> manipulator) {
		var claims = new JWTClaimsSet.Builder().subject(subject);
		manipulator.accept(claims);
		return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + signature.sign(claims.build()).serialize();
	}

	// request

	@SneakyThrows
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
	@Slf4j
	public static class TestController {

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

			return new TestResponse(path, level.toString(), mdc == null ? Map.of() : mdc);
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TestResponse {
		private String path;
		private String level;
		private Map<String, String> context = Map.of();
	}
}
