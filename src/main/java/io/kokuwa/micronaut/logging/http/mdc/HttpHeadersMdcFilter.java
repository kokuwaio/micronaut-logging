package io.kokuwa.micronaut.logging.http.mdc;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;

import io.kokuwa.micronaut.logging.http.AbstractMdcFilter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.runtime.context.scope.Refreshable;

/**
 * Filter to add http headers to MDC.
 *
 * @author Stephan Schnabel
 */
@Refreshable
@Requires(property = HttpHeadersMdcFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(property = HttpHeadersMdcFilter.PREFIX + ".names")
@Filter("${" + HttpHeadersMdcFilter.PREFIX + ".path:/**}")
public class HttpHeadersMdcFilter extends AbstractMdcFilter {

	public static final String PREFIX = "logger.http.headers";
	public static final int DEFAULT_ORDER = ServerFilterPhase.FIRST.before();

	private final Set<String> headers;
	private final String prefix;

	public HttpHeadersMdcFilter(
			@Value("${" + PREFIX + ".names}") List<String> headers,
			@Value("${" + PREFIX + ".prefix}") Optional<String> prefix,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		super(order.orElse(DEFAULT_ORDER));
		this.prefix = prefix.orElse(null);
		this.headers = headers.stream().map(String::toLowerCase).collect(Collectors.toSet());
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
		var mdc = new HashMap<String, String>();
		for (var header : headers) {
			request.getHeaders()
					.getFirst(header)
					.ifPresent(value -> mdc.put(prefix == null ? header : prefix + header, String.valueOf(value)));
		}
		return doFilter(request, chain, mdc);
	}
}
