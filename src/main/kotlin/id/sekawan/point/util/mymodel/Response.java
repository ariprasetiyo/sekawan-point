package id.sekawan.point.util.mymodel;


public class Response<T> {
    private String requestId;
    private String type;
    private Integer status;
    private String statusMessage;
    private String statusDisplayMessage;
    private String ts;
    private T body;

    public Response() {
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

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getTs() {
        return this.ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getStatusDisplayMessage() {
        return this.statusDisplayMessage;
    }

    public void setStatusDisplayMessage(String statusDisplayMessage) {
        this.statusDisplayMessage = statusDisplayMessage;
    }
}
