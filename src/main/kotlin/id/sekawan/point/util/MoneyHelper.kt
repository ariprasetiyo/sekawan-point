package id.sekawan.point.util

import java.text.NumberFormat
import java.util.Locale

class MoneyHelper {
    companion object {
        fun formatMoneyWithoutCurrency(amount: Long): String {
            val numberFormatID = NumberFormat.getInstance(Locale("id", "ID"))
            // Enable thousands grouping
            numberFormatID.isGroupingUsed = true 
            // Set the number of decimal places to 2
            numberFormatID.minimumFractionDigits = 2
            numberFormatID.maximumFractionDigits = 2

            return numberFormatID.format(amount)
        }
    }
}
