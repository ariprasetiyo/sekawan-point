package id.sekawan.point.util.mymodel;

public class Request<T> {
    private String requestId;
    private Long requestTime;
    private String type;
    private T body;

    public Request() {
    }

    public Long getRequestTime() {
        return this.requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
