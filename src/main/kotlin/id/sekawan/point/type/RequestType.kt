package id.sekawan.point.type

enum class RequestType(val alias: String, val desc: String) {

    TYPE_CLEAR_CACHE("clear_cache", "clear cache"),
    TYPE_LOGIN("login", "login"),
    TYPE_ROLE("role", "role"),
    TYPE_REGISTRATION_ROLE("registration_role", "registration_role"),
    TYPE_REGISTRATION_USER("registration_user", "registration_user"),
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