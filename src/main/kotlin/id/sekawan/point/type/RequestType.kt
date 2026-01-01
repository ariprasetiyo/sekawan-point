package id.sekawan.point.type

import com.google.gson.annotations.SerializedName

enum class RequestType(val alias: String, val desc: String) {

    @SerializedName("clear_cache")
    TYPE_CLEAR_CACHE("clear_cache", "clear cache"),
    @SerializedName("login")
    TYPE_LOGIN("login", "login"),
    @SerializedName("role")
    TYPE_ROLE("role", "role"),
    @SerializedName("registration_role")
    TYPE_REGISTRATION_ROLE("registration_role", "registration_role"),
    @SerializedName("registration_user")
    TYPE_REGISTRATION_USER("registration_user", "registration_user"),
    @SerializedName("unknown")
    TYPE_UNKNOWN("unknown", "unknown");

    companion object {
        private val aliasMap = values().associateBy { it.alias }
        fun fromAlias(alias: String): RequestType {
            return aliasMap[alias] ?: TYPE_UNKNOWN
        }
    }

    override fun toString(): String {
        return alias
    }

}