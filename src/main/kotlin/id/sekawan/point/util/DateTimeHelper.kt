package id.sekawan.point.util

import id.sekawan.point.util.mylog.LoggerFactory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import java.sql.Date
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateTimeHelper {

    companion object {
        internal var logger = LoggerFactory().createLogger(DateTimeHelper::class.java.simpleName)
        val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSSZ")
        val databaseFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ")
        val dateTimeFormatterWithMillis = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        const val DATE_FORMAT_REFERENCE_NO = "yyyyMMddHHmmssSSS"
        val timeWIBFormatter = DateTimeFormat.forPattern("HHmmss")
        val dateOnlyFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val dateWIBFormatter = DateTimeFormat.forPattern("yyyyMMdd")
        val dateTimeFormatterNoZ = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
        val dateWIBYYYYMMFormatter = DateTimeFormat.forPattern("yyyyMM")
        val yyyyMMddFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun getCurrentZonedDateTime(): ZonedDateTime {
            return ZonedDateTime.now()
        }

        fun getEndOfYesterday(): ZonedDateTime {
            return ZonedDateTime.now()
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999_999_000) // postgres only supports up to 6 decimal place, so we use 999_999_000 instead of 999_999_999 to avoid rounding up to the next day
        }

        fun getIsoLocalDate(zonedDateTime: ZonedDateTime): Date {
            return Date.valueOf(zonedDateTime.toLocalDate())
        }

        fun getIsoLocalDate(zonedDateTime: ZonedDateTime, addedDays: Long): Date {
            return Date.valueOf(zonedDateTime.plusDays(addedDays).toLocalDate())
        }

        fun getIsoLocalDate(localDate: LocalDate, addedDays: Long): Date {
            return Date.valueOf(localDate.plusDays(addedDays))
        }

        fun getIsoZonedDateTime(zonedDateTime: ZonedDateTime): Timestamp {
            return Timestamp.from(zonedDateTime.toInstant())
        }

        fun getIsoZonedDateTimeMinusDaysAndHour(zonedDateTime: ZonedDateTime, minusDays: Long, hour: Int): Timestamp {
            val adjustedTime = zonedDateTime
                .minusDays(minusDays)
                .withHour(hour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
            return Timestamp.from(adjustedTime.toInstant())
        }

        fun getDateInt(zonedDateTime: ZonedDateTime): Int {
            return zonedDateTime.format(yyyyMMddFormatter).toInt()
        }

        fun getDateInt(date: LocalDate): Int {
            return date.format(yyyyMMddFormatter).toInt()
        }

        fun getLocalDate(dateInt: Int): LocalDate {
            return LocalDate.parse(dateInt.toString(), yyyyMMddFormatter)
        }

        fun getTimeWIBInt(days: Int): Int {
            return DateTime().plusDays(days).toString(timeWIBFormatter).toInt()
        }

        fun convertLongToString(timeStamp: Long): String {
            return DateTime(timeStamp).toString(dateTimeFormatterNoZ)
        }

        fun getTimeInt(zonedDateTime: ZonedDateTime): Int {
            return zonedDateTime.format(DateTimeFormatter.ofPattern("HHmmss")).toInt()
        }

        fun getDateNow(): String {
            return DateTimeFormat.forPattern(DATE_FORMAT_REFERENCE_NO).print(DateTime.now())
        }

        fun getDateID(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
            return date.format(formatter)
        }

        private fun isoParseDateTime(datetime: String): DateTime? {
            return try {
                ISODateTimeFormat.dateTimeParser().parseDateTime(datetime)
            } catch (ex: Exception) {
                if (ex !is IllegalArgumentException) {
                    logger.warn("Failed parse ISODateTimeFormat  ${ex.message}", datetime)
                }
                return null
            }
        }

        private fun parseDateTimeWithMillis(datetime: String): DateTime? {
            try {
                return dateTimeFormatterWithMillis.parseDateTime(datetime)
            } catch (ex: Exception) {
                if (ex !is IllegalArgumentException) {
                    logger.warn("Failed parseDateTimeWithMillis ${ex.message}", datetime)
                }
                return null
            }
        }

        fun parseDateTime(datetime: String): DateTime? {
            try {
                return dateTimeFormatter.parseDateTime(datetime)
            } catch (ex: Exception) {
                if (ex is IllegalArgumentException) {
                    try {
                        return databaseFormatter.parseDateTime(datetime)
                    } catch (ex: Exception) {
                        if (ex is IllegalArgumentException) {
                            val dateTime: DateTime? = isoParseDateTime(datetime)
                            if (dateTime != null) {
                                return dateTime
                            }
                            return parseDateTimeWithMillis(datetime)
                        }
                        logger.error("Failed parse datetime format", datetime, ex)
                        return null
                    }
                } else {
                    logger.error("Failed parse datetime", datetime, ex)
                    return null
                }
            }
        }

        fun getDateWIBYYYYMMInt(datetime: DateTime): Int {
            return datetime.toString(dateWIBYYYYMMFormatter).toInt()
        }

        fun parseIntDateToZonedDateTime(dateInt: Int, zoneId: String = "Asia/Jakarta"): ZonedDateTime {
            val dateStr = dateInt.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val localDate = LocalDate.parse(dateStr, formatter)
            return localDate.atStartOfDay(ZoneId.of(zoneId))
        }

        fun offsetDateTimeJakarta(dateTime: OffsetDateTime) : OffsetDateTime {
            return dateTime.withOffsetSameInstant(ZoneOffset.ofHours(7))
        }
    }
}
