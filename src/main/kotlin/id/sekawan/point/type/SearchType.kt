package id.sekawan.point.type

enum class SearchType (val id : String, val alias: String?){

    NAME( "name", "name"),
    PHONE_NUMBER( "phone_number", "phone number"),
    USER_ID( "user_id", "user id"),
    EMAIL( "emai", "email"),
    ROLE_ID( "role_id", "role id"),
    IS_ACTIVE("is_active", "is active"),
    NONE("none", "none");

    companion object {
        private val idMap = SearchType.entries.associateBy { it.id }
        fun fromId(id: String?): SearchType? {
            if(id == null){
                return id
            }
            return idMap[id]
        }
    }

    override fun toString(): String {
        return id
    }

}