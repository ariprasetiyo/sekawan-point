package id.sekawan.point.type

enum class SideDbType (val alias : String){
    CR("credit"),
    DB("debit");

    companion object {
        fun valueOfCode(nameString: String): SideDbType {
            when (nameString) {
                "debit" -> return SideDbType.DB
                else -> return SideDbType.CR
            }
        }
    }

    override fun toString(): String {
        return alias
    }

}