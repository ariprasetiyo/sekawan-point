package id.sekawan.point.util.mylog;

public class LoggerFactory {
    public static final long DEFAULT_EXPIRE_MS = 600000L;
    private long expireMs;

    public LoggerFactory(long expireMs) {
        this.expireMs = expireMs;
    }

    public LoggerFactory() {
        this(600000L);
    }

    public EventLogger createLogger(String name) {
        return new EventLoggerImpl(name, this.expireMs);
    }
}
