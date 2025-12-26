package id.sekawan.point.util.mylib

import java.security.MessageDigest

class MyHash(private val salt: String) {

    fun md5WithSalt(input: String): String {
        return md5WithSalt(input, salt)
    }

    private fun md5WithSalt(input: String, salt: String): String {
        val md = MessageDigest.getInstance("MD5")
        val rawInput = "$input:$salt"
        val bytes = md.digest((rawInput).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}