package io.kokuwa.micronaut.logging.request;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.util.StringUtils;
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
@Requires(property = PrincipalHttpFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Filter("${" + PrincipalHttpFilter.PREFIX + ".path:/**}")
public class PrincipalHttpFilter implements HttpServerFilter {

	public static final String PREFIX = "logger.request.principal";

	public static final String DEFAULT_KEY = "principal";
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
		var princial = request.getUserPrincipal();
		if (princial.isPresent()) {
			MDC.put(key, princial.get().getName());
			return Publishers.map(chain.proceed(request), response -> {
				MDC.remove(key);
				return response;
			});
		} else {
			return chain.proceed(request);
		}
	}
}
