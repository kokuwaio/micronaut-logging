package io.kokuwa.micronaut.logging.request;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.runtime.server.EmbeddedServer;

/**
 * Http request principal filter.
 *
 * @author Stephan Schnabel
 */
@Requires(beans = EmbeddedServer.class)
@Requires(property = PrincipalHttpFilter.ENABLED, notEquals = "false")
@Filter("${" + PrincipalHttpFilter.PREFIX + ".pattern:" + PrincipalHttpFilter.DEFAULT_PATTERN + ":/**}")
public class PrincipalHttpFilter implements HttpServerFilter {

	public static final String PREFIX = "logger.request.principal";
	public static final String ENABLED = PREFIX + ".enabled";

	public static final String DEFAULT_KEY = "principal";
	public static final String DEFAULT_PATTERN = "/**";
	public static final int DEFAULT_ORDER = ServerFilterPhase.SECURITY.after();

	private final String key;
	private final int order;

	public PrincipalHttpFilter(
			@Value("${" + PREFIX + ".key:" + DEFAULT_KEY + "}") String key,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		this.key = key;
		this.order = order.orElse(DEFAULT_ORDER);
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
		request.getUserPrincipal().ifPresent(princial -> MDC.put(key, princial.getName()));
		return chain.proceed(request);
	}
}
