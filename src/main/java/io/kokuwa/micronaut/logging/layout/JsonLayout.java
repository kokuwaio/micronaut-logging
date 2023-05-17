package io.kokuwa.micronaut.logging.layout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import io.micronaut.context.exceptions.NoSuchBeanException;
import io.micronaut.http.MediaType;
import io.micronaut.json.JsonMapper;

public class JsonLayout extends LayoutBase<ILoggingEvent> {

	public static final String TIMESTAMP_ATTR_NAME = "timestamp";
	public static final String LEVEL_ATTR_NAME = "level";
	public static final String THREAD_ATTR_NAME = "thread";
	public static final String MDC_ATTR_NAME = "mdc";
	public static final String LOGGER_ATTR_NAME = "logger";
	public static final String FORMATTED_MESSAGE_ATTR_NAME = "message";
	public static final String MESSAGE_ATTR_NAME = "raw-message";
	public static final String EXCEPTION_ATTR_NAME = "exception";
	public static final String CONTEXT_ATTR_NAME = "context";

	protected boolean includeLevel = true;
	protected boolean includeThreadName = true;
	protected boolean includeMDC = true;
	protected boolean includeLoggerName = true;
	protected boolean includeFormattedMessage = true;
	protected boolean includeMessage = true;
	protected boolean includeException = true;
	protected boolean includeContextName = false;
	protected boolean includeTimestamp = true;
	private String timestampFormat;
	private String timestampFormatTimezoneId;
	private ThrowableHandlingConverter throwableHandlingConverter = new ThrowableProxyConverter();
	private JsonMapper mapper;

	@Override
	public String getContentType() {
		return MediaType.APPLICATION_JSON;
	}

	@Override
	public void start() {
		this.throwableHandlingConverter.start();
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		this.throwableHandlingConverter.stop();
	}

	@Override
	public String doLayout(ILoggingEvent event) {
		var map = toJsonMap(event);

		if (mapper == null) {
			try {
				mapper = JsonMapper.createDefault();
			} catch (NoSuchBeanException e) {
				if (!StatusUtil.contextHasStatusListener(context)) {
					addError("Failed to get object mapper from micronaut, please check your classpath");
					StatusListenerConfigHelper.addOnConsoleListenerInstance(context, new OnConsoleStatusListener());
				}
				return map.toString() + CoreConstants.LINE_SEPARATOR;
			}
		}

		try {
			return new String(mapper.writeValueAsBytes(map), StandardCharsets.UTF_8) + CoreConstants.LINE_SEPARATOR;
		} catch (IOException e) {
			addError("Failed to write json from event " + event + " and map " + map, e);
			return null;
		}
	}

	protected Map<String, Object> toJsonMap(ILoggingEvent event) {
		var map = new LinkedHashMap<String, Object>();
		addTimestamp(TIMESTAMP_ATTR_NAME, includeTimestamp, event.getTimeStamp(), map);
		add(LEVEL_ATTR_NAME, includeLevel, String.valueOf(event.getLevel()), map);
		add(THREAD_ATTR_NAME, includeThreadName, event.getThreadName(), map);
		addMap(MDC_ATTR_NAME, includeMDC, event.getMDCPropertyMap(), map);
		add(LOGGER_ATTR_NAME, includeLoggerName, event.getLoggerName(), map);
		add(FORMATTED_MESSAGE_ATTR_NAME, includeFormattedMessage, event.getFormattedMessage(), map);
		add(MESSAGE_ATTR_NAME, includeMessage, event.getMessage(), map);
		add(CONTEXT_ATTR_NAME, includeContextName, event.getLoggerContextVO().getName(), map);
		addThrowableInfo(EXCEPTION_ATTR_NAME, includeException, event, map);
		return map;
	}

	protected void addThrowableInfo(String fieldName, boolean field, ILoggingEvent value, Map<String, Object> map) {
		if (field && value != null) {
			var throwableProxy = value.getThrowableProxy();
			if (throwableProxy != null) {
				var ex = throwableHandlingConverter.convert(value);
				if (ex != null && !ex.equals("")) {
					map.put(fieldName, ex);
				}
			}
		}
	}

	protected void addMap(String key, boolean field, Map<String, ?> mapValue, Map<String, Object> map) {
		if (field && mapValue != null && !mapValue.isEmpty()) {
			map.put(key, mapValue);
		}
	}

	protected void addTimestamp(String key, boolean field, long timeStamp, Map<String, Object> map) {
		if (field) {
			var formatted = formatTimestamp(timeStamp);
			if (formatted != null) {
				map.put(key, formatted);
			}
		}
	}

	protected void add(String fieldName, boolean field, String value, Map<String, Object> map) {
		if (field && value != null) {
			map.put(fieldName, value);
		}
	}

	protected String formatTimestamp(long timestamp) {
		if (timestampFormat == null || timestamp < 0) {
			return String.valueOf(timestamp);
		}
		var date = new Date(timestamp);
		var format = new SimpleDateFormat(timestampFormat);
		if (timestampFormatTimezoneId != null) {
			format.setTimeZone(TimeZone.getTimeZone(timestampFormatTimezoneId));
		}
		return format.format(date);
	}

	// setter

	public void setIncludeLevel(boolean includeLevel) {
		this.includeLevel = includeLevel;
	}

	public void setIncludeThreadName(boolean includeThreadName) {
		this.includeThreadName = includeThreadName;
	}

	public void setIncludeMDC(boolean includeMDC) {
		this.includeMDC = includeMDC;
	}

	public void setIncludeLoggerName(boolean includeLoggerName) {
		this.includeLoggerName = includeLoggerName;
	}

	public void setIncludeFormattedMessage(boolean includeFormattedMessage) {
		this.includeFormattedMessage = includeFormattedMessage;
	}

	public void setIncludeMessage(boolean includeMessage) {
		this.includeMessage = includeMessage;
	}

	public void setIncludeException(boolean includeException) {
		this.includeException = includeException;
	}

	public void setIncludeContextName(boolean includeContextName) {
		this.includeContextName = includeContextName;
	}

	public void setIncludeTimestamp(boolean includeTimestamp) {
		this.includeTimestamp = includeTimestamp;
	}

	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public void setTimestampFormatTimezoneId(String timestampFormatTimezoneId) {
		this.timestampFormatTimezoneId = timestampFormatTimezoneId;
	}

	public void setThrowableHandlingConverter(ThrowableHandlingConverter throwableHandlingConverter) {
		this.throwableHandlingConverter = throwableHandlingConverter;
	}
}
