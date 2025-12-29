package id.sekawan.point.util.mymodel

import java.time.OffsetTime

class RoleResponseBody {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var authorization: String? = null
    var isActive: Boolean = false
    var createdAt: OffsetTime? = null
    var updatedAt: OffsetTime? = null
}