package io.kokuwa.micronaut.logging.http;

import java.util.Map;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;

/**
 * Base for all MDC related http filters.
 *
 * @author Stephan Schnabel
 */
public abstract class AbstractMdcFilter implements HttpServerFilter {

	private final int order;

	public AbstractMdcFilter(Integer order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return order;
	}

	protected Publisher<MutableHttpResponse<?>> doFilter(
			HttpRequest<?> request,
			ServerFilterChain chain,
			Map<String, String> mdc) {

		if (mdc.isEmpty()) {
			return chain.proceed(request);
		}

		mdc.forEach(MDC::put);
		return Publishers.map(chain.proceed(request), response -> {
			mdc.keySet().forEach(MDC::remove);
			return response;
		});
	}
}
