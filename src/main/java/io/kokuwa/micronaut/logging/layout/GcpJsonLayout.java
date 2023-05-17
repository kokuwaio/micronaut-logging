package io.kokuwa.micronaut.logging.layout;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.micronaut.core.util.StringUtils;

/**
 * GCP logging layout.
 *
 * @author Stephan Schnabel
 * @see "https://cloud.google.com/logging/docs/agent/configuration#process-payload"
 * @see "https://cloud.google.com/error-reporting/reference/rest/v1beta1/ServiceContext"
 */
public class GcpJsonLayout extends JsonLayout {

	private static final String UNDEFINED = "_IS_UNDEFINED";
	private static final String TIME_ATTR_NAME = "time";
	private static final String SEVERITY_ATTR_NAME = "severity";

	private Map<String, String> serviceContext;
	private String serviceName;
	private String serviceVersion;

	@Override
	protected Map<String, Object> toJsonMap(ILoggingEvent event) {
		var map = new LinkedHashMap<String, Object>();
		add(TIME_ATTR_NAME, includeTimestamp, Instant.ofEpochMilli(event.getTimeStamp()).toString(), map);
		add(SEVERITY_ATTR_NAME, includeLevel, String.valueOf(event.getLevel()), map);
		add(THREAD_ATTR_NAME, includeThreadName, event.getThreadName(), map);
		add(CONTEXT_ATTR_NAME, includeContextName, event.getLoggerContextVO().getName(), map);
		add(LOGGER_ATTR_NAME, includeLoggerName, event.getLoggerName(), map);
		addMap(MDC_ATTR_NAME, includeMDC, event.getMDCPropertyMap(), map);
		add(FORMATTED_MESSAGE_ATTR_NAME, includeFormattedMessage, event.getFormattedMessage(), map);
		add(MESSAGE_ATTR_NAME, includeMessage, event.getMessage(), map);
		addThrowableInfo(EXCEPTION_ATTR_NAME, includeException, event, map);
		addServiceContext(map);
		return map;
	}

	private void addServiceContext(Map<String, Object> map) {
		if (serviceContext == null) {
			serviceContext = new LinkedHashMap<>(2);
			if (StringUtils.isNotEmpty(serviceName) && !serviceName.endsWith(UNDEFINED)) {
				serviceContext.put("service", serviceName);
			}
			if (StringUtils.isNotEmpty(serviceVersion) && !serviceVersion.endsWith(UNDEFINED)) {
				serviceContext.put("version", serviceVersion);
			}
		}
		if (!serviceContext.isEmpty()) {
			map.put("serviceContext", serviceContext);
		}
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
}
