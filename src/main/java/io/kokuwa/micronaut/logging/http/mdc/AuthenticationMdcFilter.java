package io.kokuwa.micronaut.logging.http.mdc;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.MDC;

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
import io.micronaut.security.authentication.Authentication;

/**
 * Filter to add claims from authentication to MDC.
 *
 * @author Stephan Schnabel
 */
@Refreshable
@Requires(classes = Authentication.class)
@Requires(property = AuthenticationMdcFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Filter("${" + AuthenticationMdcFilter.PREFIX + ".path:/**}")
public class AuthenticationMdcFilter extends AbstractMdcFilter {

	public static final String PREFIX = "logger.http.authentication";
	public static final String DEFAULT_NAME = "principal";
	public static final int DEFAULT_ORDER = ServerFilterPhase.SECURITY.after();

	private final String name;
	private final List<String> attributes;
	private final String prefix;

	public AuthenticationMdcFilter(
			@Value("${" + PREFIX + ".name:principal}") Optional<String> name,
			@Value("${" + PREFIX + ".attributes:[]}") List<String> attributes,
			@Value("${" + PREFIX + ".prefix}") Optional<String> prefix,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		super(order.orElse(DEFAULT_ORDER));
		this.name = name.orElse(DEFAULT_NAME);
		this.prefix = prefix.orElse(null);
		this.attributes = attributes;
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {

		// get authentication

		var optional = request.getUserPrincipal(Authentication.class);
		if (optional.isEmpty()) {
			return chain.proceed(request);
		}
		var authentication = optional.get();
		var authenticationAttributes = authentication.getAttributes();

		// add mdc

		var mdc = new HashMap<String, String>();
		MDC.put(prefix == null ? name : prefix + name, authentication.getName());
		for (var header : attributes) {
			var value = authenticationAttributes.get(header);
			if (value != null) {
				mdc.put(prefix == null ? header : prefix + header, String.valueOf(value));
			}
		}

		return doFilter(request, chain, mdc);
	}
}
