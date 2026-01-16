package id.sekawan.point.util.mymodel

import java.time.OffsetDateTime

data class Menu (
    var id : Int,
    var name : String?,
    var parent : Int?,
    var description : String?,
    var icon : String?,
    var url : String?,
    var isActive : Boolean?,
    var createdAt : OffsetDateTime? = null,
    var updatedAt : OffsetDateTime? = null
)