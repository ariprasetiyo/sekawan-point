package id.sekawan.point.util.mylib;

import com.google.gson.*;
import org.joda.money.Money;

import java.lang.reflect.Type;

public class MoneyAdapter implements JsonSerializer<Money>,JsonDeserializer<Money> {

    @Override
    public Money deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Money.parse(json.getAsString());
        } catch (IllegalArgumentException ex) {
            return null;
        } catch (ArithmeticException e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(Money src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
