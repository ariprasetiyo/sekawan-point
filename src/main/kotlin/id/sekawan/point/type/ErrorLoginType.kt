package id.sekawan.point.type

enum class ErrorLoginType (val errorCode : Int, val errorMessage : String, val desc : String){

    UNAUTHORIZED(100, "Unauthorized", "Roles should be set"),
    FORBIDDEN(101, "Forbidden", "User haven't access resource"),
    INVALID_AUTHENTICATION_102(102, "invalid authentication", "session null"),
    INVALID_AUTHENTICATION_103(103, "invalid authentication", "token jwt invalid");

    companion object {
        private val errorMessageMap = values().associateBy { it.errorMessage }
        fun fromAlias(alias: String): ErrorLoginType {
            return errorMessageMap[alias] ?: FORBIDDEN
        }
    }

    override fun toString(): String {
        return errorMessage
    }

}