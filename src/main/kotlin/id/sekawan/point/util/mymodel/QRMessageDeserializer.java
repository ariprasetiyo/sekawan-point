package id.sekawan.point.util.mymodel;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by nanda on 11/17/16.
 */
public class QRMessageDeserializer implements JsonDeserializer<MessageIn> {
    public static final String TYPE_INQUIRY = "qr_inquiry";
    public static final String TYPE_INVOICE_PAY = "qr_invoice_pay";
    public static final String TYPE_INVOICE_PAY_VOUCHER = "qr_invoice_pay_voucher";
    public static final String TYPE_PING = "qr_ping";
    public static final String TYPE_CREATE = "qr_create";
    public static final String TYPE_CREATE_SKU = "qr_create_sku";
    public static final String TYPE_LIST_SKU = "qr_list_sku";
    public static final String TYPE_ORDER_CREATE = "qr_order_create";
    public static final String TYPE_LIST_ORDER = "qr_list_order";
    public static final String TYPE_PRODUCT_PAY = "qr_product_pay";
    public static final String TYPE_MERCHANT_INVOICE = "merchant_invoice";
    public static final String TYPE_QRIS_TRANSFER = "qris_transfer";
    public static final String TYPE_INQUIRY_TRANS = "qr_inquiry_translator";


    @Override
    public MessageIn deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement type = json.getAsJsonObject().get(MessageIn.KEY_TYPE);
        MessageIn message = null;
        if (type != null) {
            Class<? extends MessageIn> clazz = getMessageClass(type.getAsString());
            if (clazz != null) {
                message = context.deserialize(json, clazz);
            }
        }
        return message;
    }

    private Class<? extends MessageIn> getMessageClass(String type) {
        if (TYPE_INQUIRY.equals(type) || TYPE_INQUIRY_TRANS.equals(type)) {
            return MessageIn.class;
        } else if (MessageIn.TYPE_P2P_COUPON.equals(type)) {
            return MessageIn.class;
        } else {
            return null;
        }
    }
}
