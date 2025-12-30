package id.sekawan.point.util.mylib

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.sekawan.point.util.mymodel.MessageIn
import id.sekawan.point.util.mymodel.QRMessageDeserializer
import org.joda.money.Money
import org.joda.time.DateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime

object GsonHelper {

    fun createGson(): Gson {
        return GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(Money::class.java, MoneyAdapter())
            .registerTypeAdapter(MessageIn::class.java, QRMessageDeserializer())
            .registerTypeAdapter(OffsetTime ::class.java, OffsetTimeAdapter())
            .registerTypeAdapter(OffsetDateTime ::class.java, OffsetDateTimeAdapter())
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
            .create()
    }

    fun createGsonSerializeNull(): Gson {
        return GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(Money::class.java, MoneyAdapter())
            .registerTypeAdapter(MessageIn::class.java, QRMessageDeserializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeSerializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
            .registerTypeAdapterFactory(SerializableAsNullConverter())
            .create()
    }
}