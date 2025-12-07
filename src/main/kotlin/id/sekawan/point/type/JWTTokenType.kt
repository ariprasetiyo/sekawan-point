package id.sekawan.point.type

enum class JWTTokenType ( val alias : String, val desc : String){

    ACCESS( "access", "token for authentication"),
    REFRESH( "refresh", "token refresh for generate token access"),
    EMPTY("empty", "default, can't access");

    companion object {
        private val aliasMap = values().associateBy { it.alias }
        fun fromAlias(alias: String): JWTTokenType {
            return aliasMap[alias] ?: EMPTY
        }
    }

    override fun toString(): String {
        return alias
    }

}