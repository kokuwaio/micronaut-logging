package io.kokuwa.micronaut.logging.http.mdc;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

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
	private final Set<String> attributes;

	public AuthenticationMdcFilter(
			@Value("${" + PREFIX + ".name}") Optional<String> name,
			@Value("${" + PREFIX + ".attributes}") Optional<Set<String>> attributes,
			@Value("${" + PREFIX + ".prefix}") Optional<String> prefix,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		super(order.orElse(DEFAULT_ORDER), prefix.orElse(null));
		this.name = name.orElse(DEFAULT_NAME);
		this.attributes = attributes.orElseGet(Set::of);
		if (name.isPresent() || !this.attributes.isEmpty()) {
			log.info("Configured with name {} and attributes {}", name, attributes);
		}
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {

		// get authentication

		var authenticationOptional = request.getUserPrincipal(Authentication.class);
		if (authenticationOptional.isEmpty()) {
			return chain.proceed(request);
		}
		var authentication = authenticationOptional.get();
		var authenticationAttributes = authentication.getAttributes();

		// add mdc

		var mdc = new HashMap<String, String>();
		mdc.put(name, authentication.getName());
		for (var attibuteName : attributes) {
			var attibuteValue = authenticationAttributes.get(attibuteName);
			if (attibuteValue != null) {
				mdc.put(attibuteName, String.valueOf(attibuteValue));
			}
		}

		return doFilter(request, chain, mdc);
	}
}
