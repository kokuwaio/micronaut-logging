package io.kokuwa.micronaut.logging.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import ch.qos.logback.classic.Level;
import io.kokuwa.micronaut.logging.request.TestController.TestResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Contoller for testing {@link HeaderLoggingServerHttpFilter} and {@link PrincipalHttpFilter}.
 *
 * @author Stephan Schnabel
 */
@Singleton
public class TestClient {

	@Inject
	@Client("/")
	HttpClient client;
	@Inject
	SignatureGeneratorConfiguration signature;

	String token(String subject) {
		try {
			return signature.sign(new JWTClaimsSet.Builder().subject(subject).build()).serialize();
		} catch (JOSEException e) {
			fail("failed to create token");
			return null;
		}
	}

	TestResponse get(String token, String header) {

		var request = HttpRequest.GET("/");
		if (token != null) {
			request.bearerAuth(token);
		}
		if (header != null) {
			request.getHeaders().add(HeaderLoggingServerHttpFilter.DEFAULT_HEADER, header);
		}

		var response = client.toBlocking().exchange(request, TestResponse.class);
		assertEquals(HttpStatus.OK, response.getStatus(), "status");
		assertTrue(response.getBody().isPresent(), "body");

		return response.body();
	}

	void assertLevel(Level expectedLevel, String actualTokenValue, String actualHeaderValue) {
		assertEquals(expectedLevel.toString(), get(actualTokenValue, actualHeaderValue).getLevel());
	}
}
