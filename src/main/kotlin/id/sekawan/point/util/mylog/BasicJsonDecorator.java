package id.sekawan.point.util.mylog;


import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Iterator;
import java.util.List;
import net.logstash.logback.decorate.JsonFactoryDecorator;

public class BasicJsonDecorator implements JsonFactoryDecorator {
    private int maxArraySize;

    public BasicJsonDecorator() {
    }

    public void setMaxArraySize(int maxArraySize) {
        this.maxArraySize = maxArraySize;
    }

    public MappingJsonFactory decorate(MappingJsonFactory factory) {
        ObjectMapper codec = factory.getCodec();
        codec.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        codec.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        codec.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        SimpleModule module = new SimpleModule("mylog", new Version(1, 0, 0, (String)null, (String)null, (String)null));
        module.addSerializer(List.class, new ListSerializer(this.maxArraySize));
        this.addSerializers(module, this.getSerializers());
        codec.registerModule(module);
        codec.setSerializationInclusion(Include.NON_NULL);
        return factory;
    }

    public List<JsonSerializer> getSerializers() {
        return null;
    }

    private void addSerializers(SimpleModule module, List<JsonSerializer> serializers) {
        if (serializers != null) {
            Iterator var3 = serializers.iterator();

            while(var3.hasNext()) {
                JsonSerializer serializer = (JsonSerializer)var3.next();
                module.addSerializer(serializer);
            }
        }

    }
}
