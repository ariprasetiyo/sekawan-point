package id.sekawan.point.util.mylog;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class ListSerializer extends JsonSerializer<List> {
    private static final int DEFAULT_MAX_ARRAY_SIZE = 20;
    private int maxSize = DEFAULT_MAX_ARRAY_SIZE;

    public ListSerializer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void serialize(List value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartArray();

        int listOriginalSize = value.size();
        int startIndex = Math.max(listOriginalSize - maxSize, 0);

        for (int i = startIndex; i < listOriginalSize; i++) {
            gen.writeObject(value.get(i));
        }

        gen.writeEndArray();

        if(listOriginalSize > maxSize) {
            gen.writeRaw(getTruncatedInfoJson(gen.getOutputContext().getCurrentName(), listOriginalSize));
        }
    }

    private String getTruncatedInfoJson(String fieldName, int originalSize) {
        StringBuilder builder = new StringBuilder();
        builder.append(",\"");
        builder.append(fieldName);
        builder.append("_truncated\":\"showing last ");
        builder.append(String.valueOf(maxSize));
        builder.append(" of ");
        builder.append(String.valueOf(originalSize));
        builder.append("\"");

        return builder.toString();
    }
}
