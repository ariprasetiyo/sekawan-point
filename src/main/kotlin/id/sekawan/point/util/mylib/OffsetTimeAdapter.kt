package id.sekawan.point.util.mylib

import com.google.gson.*
import java.lang.reflect.Type
import java.time.OffsetTime

class OffsetTimeAdapter : JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {

    override fun serialize(
        src: OffsetTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OffsetTime {
        return OffsetTime.parse(json.asString)
    }
}