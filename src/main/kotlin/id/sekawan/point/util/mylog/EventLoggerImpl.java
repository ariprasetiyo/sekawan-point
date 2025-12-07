package id.sekawan.point.util.mylog;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoggerImpl implements EventLogger {

    private LoadingCache<String, Long> startTimeCache;
    private Timer timer;
    private long expireTime;
    private int maxMessageLength;
    private Logger logger;
    private BaseLogger infoLogger;
    private BaseLogger warnLogger;
    private BaseLogger emptyMessageInfoLogger;
    private BaseLogger emptyMessageWarnLogger;
    private String entityName;

    public EventLoggerImpl(String name, long expireTime) {
        this.expireTime = expireTime;
        this.startTimeCache = this.createCache(expireTime);
        this.timer = new TimerImpl();
        this.maxMessageLength = -1;
        this.entityName = name;
        this.logger = LoggerFactory.getLogger("mylog." + name);
        this.infoLogger = (marker, format, message) -> {
            this.logger.info(marker, format, message);
        };
        this.warnLogger = (marker, format, message) -> {
            this.logger.warn(marker, format, message);
        };
        this.emptyMessageInfoLogger = (marker, format, message) -> {
            this.logger.info(marker, (String)null);
        };
        this.emptyMessageWarnLogger = (marker, format, message) -> {
            this.logger.warn(marker, (String)null);
        };
    }

    public EventLoggerImpl(String name, long expireTime, Timer timer) {
        this(name, expireTime);
        this.timer = timer;
    }

    private LoadingCache<String, Long> createCache(long expireTime) {
        return CacheBuilder.newBuilder().expireAfterWrite(expireTime, TimeUnit.MILLISECONDS).build(new CacheLoader<String, Long>() {
            public Long load(String string) throws Exception {
                return 0L;
            }
        });
    }

    private Map<String, Object> getMarker(String eventName) {
        StringBuilder eventNameBuilder = new StringBuilder();
        eventNameBuilder.append(this.entityName);
        eventNameBuilder.append('.');
        eventNameBuilder.append(eventName);
        Map<String, Object> markers = new HashMap();
        markers.put("event", eventNameBuilder.toString());
        return markers;
    }

    private Map<String, Object> getMarker() {
        Map<String, Object> markers = new HashMap();
        markers.put("event", this.entityName);
        return markers;
    }

    public void info(String message) {
        this.doLog(this.getMarker(), message, this.infoLogger);
    }

    public void info(String eventName, Object message) {
        this.doLog(this.getMarker(eventName), message, this.infoLogger);
    }

    public void info(String eventName, Object message, Throwable e) {
        this.logger.info(Markers.appendEntries(this.getMarkerWithMessage(eventName, message)), "", e);
    }

    public void warn(String message) {
        this.doLog(this.getMarker(), message, this.warnLogger);
    }

    public void warn(String eventName, Object message) {
        this.doLog(this.getMarker(eventName), message, this.warnLogger);
    }

    public void warn(String eventName, Object message, Throwable e) {
        this.logger.warn(Markers.appendEntries(this.getMarkerWithMessage(eventName, message)), "", e);
    }

    public void error(String eventName, Object message, Throwable e) {
        this.logger.error(Markers.appendEntries(this.getMarkerWithMessage(eventName, message)), "", e);
    }

    private Map<String, Object> getMarkerWithMessage(String eventName, Object message) {
        Map<String, Object> marker = this.getMarker(eventName);
        marker.put("message", message);
        return marker;
    }

    public LogContext startLogContext(String eventName) {
        LogContextImpl logContextImpl = new LogContextImpl(this);
        return logContextImpl.startInfo(eventName);
    }

    public LogContext startLogContext(String eventName, Object message) {
        LogContextImpl logContextImpl = new LogContextImpl(this);
        return logContextImpl.startInfo(eventName, message);
    }

    public void startLog(String key, String eventName) {
        this.startLog(key, eventName, (Object)null, this.emptyMessageInfoLogger);
    }

    public void startLog(String key, String eventName, Object message) {
        this.startLog(key, eventName, message, this.infoLogger);
    }

    private void startLog(String key, String eventName, Object message, BaseLogger logger) {
        this.startTimeCache.put(key, this.timer.getCurrentTimeMs());
        Map<String, Object> markers = this.getMarker(eventName);
        markers.put("key", key);
        this.doLog(markers, message, logger);
    }

    public void finishLog(String key, String eventName) {
        this.finishLog(key, eventName, (Object)null, true, this.emptyMessageInfoLogger);
    }

    public void finishLog(String key, String eventName, Object message) {
        this.finishLog(key, eventName, message, true, this.infoLogger);
    }

    public void finishLog(String key, String eventName, Object message, boolean invalidateStartTime) {
        this.finishLog(key, eventName, message, invalidateStartTime, this.infoLogger);
    }

    private void finishLog(String key, String eventName, Object message, boolean invalidateStartTime, BaseLogger logger) {
        Map<String, Object> markers = this.getMarker(eventName);
        Long startTime = (Long)this.startTimeCache.getIfPresent(key);
        if (startTime != null) {
            long latency = this.timer.getCurrentTimeMs() - startTime;
            markers.put("latency", latency);
        } else {
            markers.put("latencyExpired", ">" + this.expireTime);
        }

        markers.put("key", key);
        this.doLog(markers, message, logger);
        if (invalidateStartTime) {
            this.startTimeCache.invalidate(key);
        }

    }

    private void doLog(Map<String, Object> marker, Object message, BaseLogger logger) {
        String traceId = this.getCurrentTraceId();
        marker.put("traceId", traceId);
        if (message == null) {
            logger.log(Markers.appendEntries(marker), (String)null, (Object)null);
        } else if (message instanceof String) {
            logger.log(Markers.appendEntries(marker), "{}", this.formatStringMessage(message));
        } else if (message instanceof Object[]) {
            logger.log(Markers.appendEntries(marker), "{}", message);
        } else {
            logger.log(Markers.appendEntries(marker), "", StructuredArguments.fields(message));
        }

    }

    private String getCurrentTraceId() {
        SpanContext ctx = Span.current().getSpanContext();
        return ctx.isValid() ? ctx.getTraceId() : "N/A";
    }

    public void setMaxMessageLength(int length) {
        this.maxMessageLength = length;
    }

    private String formatStringMessage(Object object) {
        if (object == null) {
            return "";
        } else {
            String string = object.toString();
            if (this.maxMessageLength >= 0 && string.length() > this.maxMessageLength) {
                String var10000 = string.substring(0, this.maxMessageLength);
                string = var10000 + "...";
            }

            return string;
        }
    }

    public void invalidateStartTime(String key) {
        this.startTimeCache.invalidate(key);
    }
}

