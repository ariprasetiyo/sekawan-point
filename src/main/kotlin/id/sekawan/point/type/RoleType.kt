package id.sekawan.point.type

enum class RoleType (val id : String, val alias: String?){

    SUPER_ADMIN( "super_admin", "super admin"),
    ADMIN( "admin", "admin"),
    APPROVAL( "approval", "approval"),
    MAKER( "maker", "maker"),
    READ_ONLY( "read_only", "read only"),
    GUEST_USER("guest", "guest"),
    BASIC_USER("basic", "basic"),
    PREMIUM_USER("premium", "premium"),
    BLOCK_USER("block_user", "block user");

    companion object {
        private val idMap = values().associateBy { it.id }
        fun fromId(id: String?): RoleType? {
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