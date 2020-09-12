package io.kokuwa.micronaut.logging.request;

import java.util.Optional;

import org.reactivestreams.Publisher;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.micronaut.http.filter.ServerFilterPhase;

/**
 * Http request logging filter.
 *
 * @author Stephan Schnabel
 */
@Requires(beans = HeaderLoggingServerHttpFilter.class)
@Requires(property = HeaderLoggingClientHttpFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Filter("${" + HeaderLoggingClientHttpFilter.PREFIX + ".path:/**}")
public class HeaderLoggingClientHttpFilter implements HttpClientFilter {

	public static final String PREFIX = "logger.request.propagation";
	public static final int DEFAULT_ORDER = ServerFilterPhase.TRACING.order();

	private final String serverHeader;
	private final String propagationHeader;
	private final int order;

	public HeaderLoggingClientHttpFilter(
			@Value("${" + HeaderLoggingServerHttpFilter.PREFIX + ".header}") Optional<String> serverHeader,
			@Value("${" + PREFIX + ".header}") Optional<String> propagationHeader,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		this.serverHeader = serverHeader.orElse(HeaderLoggingServerHttpFilter.DEFAULT_HEADER);
		this.propagationHeader = propagationHeader.orElse(this.serverHeader);
		this.order = order.orElse(DEFAULT_ORDER);
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> targetRequest, ClientFilterChain chain) {
		ServerRequestContext.currentRequest()
				.flatMap(currentRequest -> currentRequest.getHeaders().getFirst(serverHeader))
				.ifPresent(level -> targetRequest.getHeaders().add(propagationHeader, level));
		return chain.proceed(targetRequest);
	}
}
