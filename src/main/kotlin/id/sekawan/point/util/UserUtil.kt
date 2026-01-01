package id.sekawan.point.util

import org.apache.commons.lang3.RandomStringUtils
import org.joda.time.DateTime

class UserUtil {
    fun generateUserId(): String {
        val dateTime = DateTimeHelper.getDateWIBYYYYMMInt(datetime = DateTime.now()).toString()
        return dateTime + RandomStringUtils.random(10, true, true)
    }

}