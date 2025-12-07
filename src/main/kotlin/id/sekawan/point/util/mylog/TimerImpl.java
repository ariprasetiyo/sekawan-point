package id.sekawan.point.util.mylog;

public class TimerImpl implements Timer {
    public TimerImpl() {
    }

    public long getCurrentTimeMs() {
        return System.currentTimeMillis();
    }
}
