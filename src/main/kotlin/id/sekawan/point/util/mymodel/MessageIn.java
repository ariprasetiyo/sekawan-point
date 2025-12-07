package id.sekawan.point.util.mymodel;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageIn<T> implements Taggable {

    public static final String KEY_TYPE = "type";
    public static final String TYPE_LIKE_COUPON = "like_coupon";
    public static final String TYPE_P2P_COUPON = "p2p_coupon";
    public static final String TYPE_QR_CHECK_STATUS = "qr_check_status";
    public static final String TYPE_MERCHANT_INVOICE = "merchant_invoice";

    private String msgId;
    private String type;
    private T details;
    private transient Map<String, Object> tagMap = new ConcurrentHashMap<String, Object>();

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getDetails() {
        return details;
    }

    public void setDetails(T details) {
        this.details = details;
    }

    @Override
    public Object getTag(String key) {
        return tagMap.get(key);
    }

    @Override
    public void putTag(String key, Object value) {
        tagMap.put(key, value);
    }
}
