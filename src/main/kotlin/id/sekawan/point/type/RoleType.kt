package id.sekawan.point.type

enum class RoleType (val alias : String){

    SUPER_ADMIN( "access"),
    ADMIN( "refresh"),
    APPROVAL( "approval"),
    MAKER( "maker"),
    READ_ONLY( "read_only"),
    GUEST_USER("guest_user"),
    BASIC_USER("basic_user"),
    PREMIUM_USER("premium_user"),
    BLOCK_USER("block_users");

    companion object {
        private val aliasMap = values().associateBy { it.alias }
        fun fromAlias(alias: String): RoleType? {
            return aliasMap[alias]
        }
    }

    override fun toString(): String {
        return alias
    }

}