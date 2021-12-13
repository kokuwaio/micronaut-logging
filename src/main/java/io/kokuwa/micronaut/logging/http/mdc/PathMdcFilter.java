package io.kokuwa.micronaut.logging.http.mdc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import lombok.extern.slf4j.Slf4j;

/**
 * Filter to add request path parts to MDC.
 *
 * @author Stephan Schnabel
 */
@Refreshable
@Requires(property = PathMdcFilter.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(property = PathMdcFilter.PREFIX + ".patterns")
@Filter("${" + PathMdcFilter.PREFIX + ".path:/**}")
@Slf4j
public class PathMdcFilter extends AbstractMdcFilter {

	public static final String PREFIX = "logger.http.path";
	public static final int DEFAULT_ORDER = ServerFilterPhase.FIRST.before();
	public static final Pattern PATTERN_GROUPS = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]+)>");

	private final Map<Pattern, Set<String>> patternsWithGroups;

	public PathMdcFilter(
			@Value("${" + PREFIX + ".patterns}") List<String> patterns,
			@Value("${" + PREFIX + ".prefix}") Optional<String> prefix,
			@Value("${" + PREFIX + ".order}") Optional<Integer> order) {
		super(order.orElse(DEFAULT_ORDER), prefix.orElse(null));
		this.patternsWithGroups = new HashMap<>();
		for (var patternString : patterns) {
			try {
				var pattern = Pattern.compile(patternString);
				var groupMatcher = PATTERN_GROUPS.matcher(pattern.toString());
				var groups = new HashSet<String>();
				while (groupMatcher.find()) {
					groups.add(groupMatcher.group(1));
				}

				if (groups.isEmpty()) {
					log.warn("Path {} is missing groups.", patternString);
				} else {
					log.info("Added path {} with groups {}.", patternString, groups);
					patternsWithGroups.put(pattern, groups);
				}
			} catch (PatternSyntaxException e) {
				log.warn("Path {} is invalid.", patternString);
			}
		}
	}

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {

		var mdc = new HashMap<String, String>();
		var path = request.getPath();

		for (var patternWithGroup : patternsWithGroups.entrySet()) {
			var matcher = patternWithGroup.getKey().matcher(path);
			if (matcher.matches()) {
				for (var group : patternWithGroup.getValue()) {
					mdc.put(group, matcher.group(group));
				}
			}
		}

		return doFilter(request, chain, mdc);
	}
}
