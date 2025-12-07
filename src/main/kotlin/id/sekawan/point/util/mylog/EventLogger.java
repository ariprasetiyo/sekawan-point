package id.sekawan.point.util.mylog;

public interface EventLogger {
    void info(String var1);

    void info(String var1, Object var2);

    void info(String var1, Object var2, Throwable var3);

    void warn(String var1);

    void warn(String var1, Object var2);

    void warn(String var1, Object var2, Throwable var3);

    void error(String var1, Object var2, Throwable var3);

    LogContext startLogContext(String var1);

    LogContext startLogContext(String var1, Object var2);

    void startLog(String var1, String var2);

    void startLog(String var1, String var2, Object var3);

    void finishLog(String var1, String var2);

    void finishLog(String var1, String var2, Object var3);

    void finishLog(String var1, String var2, Object var3, boolean var4);

    void invalidateStartTime(String var1);

    void setMaxMessageLength(int var1);
}

