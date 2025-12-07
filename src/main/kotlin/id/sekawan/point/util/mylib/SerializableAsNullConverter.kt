package id.sekawan.point.util.mylib

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Field

class SerializableAsNullConverter : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        fun Field.serializedName() = declaredAnnotations
            .filterIsInstance<SerializedName>()
            .firstOrNull()?.value ?: name
        val declaredFields = type.rawType.declaredFields
        val nullableFieldNames = declaredFields
            .filter { it.declaredAnnotations.filterIsInstance<SerializeNull>().isNotEmpty() }
            .map { it.serializedName() }
        val nonNullableFields = declaredFields.map { it.serializedName() } - nullableFieldNames

        return if (nullableFieldNames.isEmpty()) {
            null
        } else object : TypeAdapter<T>() {
            private val delegateAdapter = gson.getDelegateAdapter(this@SerializableAsNullConverter, type)
            private val elementAdapter = gson.getAdapter(JsonElement::class.java)

            override fun write(writer: JsonWriter, value: T?) {
                val jsonObject = delegateAdapter.toJsonTree(value).asJsonObject
                nonNullableFields
                    .filter { jsonObject.get(it) is JsonNull }
                    .forEach { jsonObject.remove(it) }
                val originalSerializeNulls = writer.serializeNulls
                writer.serializeNulls = true
                elementAdapter.write(writer, jsonObject)
                writer.serializeNulls = originalSerializeNulls
            }

            override fun read(reader: JsonReader): T {
                return delegateAdapter.read(reader)
            }
        }
    }
}