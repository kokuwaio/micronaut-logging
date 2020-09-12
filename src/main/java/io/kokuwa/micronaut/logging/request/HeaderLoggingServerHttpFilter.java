package io.kokuwa.micronaut.logging.request;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import ch.qos.logback.classic.turbo.TurboFilter;
import io.kokuwa.micronaut.logging.LogbackUtil;
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
 * Http request logging filter.
 *
 * @author Stephan Schnabel
 */
@Requires(beans = EmbeddedServer.class)
@Requires(property = HeaderLoggingServerHttpFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Filter("${" + HeaderLoggingServerHttpFilter.PREFIX + ".path:/**}")
public class HeaderLoggingServerHttpFilter implements HttpServerFilter {

	public static final String PREFIX = "logger.request.filter";
	public static final String MDC_FILTER = PREFIX;
	public static final String MDC_KEY = "level";

	public static final String DEFAULT_HEADER = "x-log-level";
	public static final int DEFAULT_ORDER = ServerFilterPhase.FIRST.before();

	private final LogbackUtil logback;
	private final String header;
	private final int order;

	public HeaderLoggingServerHttpFilter(
			LogbackUtil logback,
			@Value("${" + PREFIX + ".header}") Optional<String> header,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		this.logback = logback;
		this.header = header.orElse(DEFAULT_HEADER);
		this.order = order.orElse(DEFAULT_ORDER);
	}

	@PostConstruct
	void startTurbofilter() {
		logback.getTurboFilter(HeaderLoggingTurboFilter.class, MDC_FILTER, HeaderLoggingTurboFilter::new).start();
	}

	@PreDestroy
	void stopTurbofilter() {
		logback.getTurboFilter(HeaderLoggingTurboFilter.class, MDC_FILTER).ifPresent(TurboFilter::stop);
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
		var level = request.getHeaders().getFirst(header);
		if (level.isPresent()) {
			MDC.put(MDC_KEY, level.get());
			return Publishers.map(chain.proceed(request), response -> {
				MDC.remove(MDC_KEY);
				return response;
			});
		} else {
			return chain.proceed(request);
		}
	}
}
