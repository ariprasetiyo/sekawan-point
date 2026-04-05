package id.sekawan.point.type

import com.google.gson.annotations.SerializedName

enum class SearchType (val id : String, val alias: String?){

    @SerializedName("id")
    ID( "id", "id"),
    @SerializedName("desc")
    DESC( "desc", "description"),
    @SerializedName("name")
    NAME( "name", "name"),
    @SerializedName("authorization")
    AUTHORIZATION( "authorization", "authorization"),
    @SerializedName("username")
    USERNAME( "username", "username"),
    @SerializedName("phone_number")
    PHONE_NUMBER( "phone_number", "phone number"),
    @SerializedName("user_id")
    USER_ID( "user_id", "user id"),
    @SerializedName("emai")
    EMAIL( "emai", "email"),
    @SerializedName("role_id")
    ROLE_ID( "role_id", "role id"),
    @SerializedName("is_active")
    IS_ACTIVE("is_active", "is active"),
    @SerializedName("all")
    ALL("all", "all"),
    @SerializedName("none")
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