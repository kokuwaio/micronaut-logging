package io.kokuwa.micronaut.logging.http.level;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import ch.qos.logback.classic.turbo.TurboFilter;
import io.kokuwa.micronaut.logging.LogbackUtil;
import io.kokuwa.micronaut.logging.http.AbstractMdcFilter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.runtime.context.scope.Refreshable;

/**
 * Http request logging filter.
 *
 * @author Stephan Schnabel
 */
@Refreshable
@Requires(property = LogLevelServerFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Filter("${" + LogLevelServerFilter.PREFIX + ".path:/**}")
public class LogLevelServerFilter extends AbstractMdcFilter {

	public static final String PREFIX = "logger.http.level";
	public static final String DEFAULT_HEADER = "x-log-level";
	public static final int DEFAULT_ORDER = ServerFilterPhase.FIRST.before();
	public static final String MDC_KEY = "level";
	public static final String MDC_FILTER = PREFIX;

	private final LogbackUtil logback;
	private final String header;

	public LogLevelServerFilter(
			LogbackUtil logback,
			@Value("${" + PREFIX + ".header}") Optional<String> header,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		super(order.orElse(DEFAULT_ORDER));
		this.logback = logback;
		this.header = header.orElse(DEFAULT_HEADER);
	}

	@PostConstruct
	void startTurbofilter() {
		logback.getTurboFilter(LogLevelTurboFilter.class, MDC_FILTER, LogLevelTurboFilter::new).start();
	}

	@PreDestroy
	void stopTurbofilter() {
		logback.getTurboFilter(LogLevelTurboFilter.class, MDC_FILTER).ifPresent(TurboFilter::stop);
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
