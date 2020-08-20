package io.kokuwa.micronaut.logging.request;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.turbo.TurboFilter;
import io.kokuwa.micronaut.logging.LogbackUtil;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.runtime.server.EmbeddedServer;

/**
 * Http request logging filter.
 *
 * @author Stephan Schnabel
 */
@Requires(beans = EmbeddedServer.class)
@Requires(property = RequestLoggingHttpFilter.ENABLED, notEquals = "false")
@Filter("${" + RequestLoggingHttpFilter.PREFIX + ".pattern:" + RequestLoggingHttpFilter.DEFAULT_PATTERN + ":/**}")
public class RequestLoggingHttpFilter implements HttpServerFilter {

	public static final String PREFIX = "logger.request";
	public static final String ENABLED = PREFIX + ".enabled";
	public static final String MDC_FILTER_NAME = PREFIX + ".filter";
	public static final String MDC_KEY = "level";

	public static final String DEFAULT_HEADER = "x-log-level";
	public static final String DEFAULT_PATTERN = "/**";

	private final LogbackUtil logback;
	private final String header;
	private final int order;

	public RequestLoggingHttpFilter(
			LogbackUtil logback,
			@Value("${" + PREFIX + ".header:" + DEFAULT_HEADER + "}") String header,
			@Value("${" + PREFIX + ".order:" + Ordered.HIGHEST_PRECEDENCE + "}") int order) {
		this.logback = logback;
		this.header = header;
		this.order = order;
	}

	@PostConstruct
	void startTurbofilter() {
		logback.getTurboFilter(RequestLoggingTurboFilter.class, MDC_FILTER_NAME, RequestLoggingTurboFilter::new).start();
	}

	@PreDestroy
	void stopTurbofilter() {
		logback.getTurboFilter(RequestLoggingTurboFilter.class, MDC_FILTER_NAME).ifPresent(TurboFilter::stop);
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
		var level = request.getHeaders().getFirst(header).map(Level::valueOf);
		if (level.isPresent()) {
			MDC.put(MDC_KEY, level.get().toString());
		}
		return chain.proceed(request);
	}
}
