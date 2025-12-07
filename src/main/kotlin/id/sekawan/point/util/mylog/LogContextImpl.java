package id.sekawan.point.util.mylog;

import java.util.UUID;

public class LogContextImpl implements LogContext {
    private static final int CHAR_COUNT_FROM_BEHIND = 6;
    private static final String LOG_KEY = "log_";
    private final EventLogger eventLogger;
    private String logKey;

    public LogContextImpl(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
        this.logKey = this.getUniqueKey();
    }

    private String getUniqueKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(uuid.length() - 6);
    }

    LogContext startInfo(String eventName, Object message) {
        this.eventLogger.startLog("log_" + this.logKey, eventName, message);
        return this;
    }

    LogContext startInfo(String eventName) {
        this.eventLogger.startLog("log_" + this.logKey, eventName);
        return this;
    }

    public void finishLog(String eventName, Object message) {
        this.eventLogger.finishLog("log_" + this.logKey, eventName, message);
    }

    public void finishLog(String eventName) {
        this.eventLogger.finishLog("log_" + this.logKey, eventName);
    }
}

