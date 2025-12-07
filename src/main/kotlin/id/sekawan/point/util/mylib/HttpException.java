package id.sekawan.point.util.mylib;

public class HttpException extends RuntimeException {
    private int httpStatusCode;

    public HttpException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }
}