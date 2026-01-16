package id.sekawan.point.util.mymodel

import java.time.OffsetDateTime

data class RoleAuthorization(
    var id: String,
    var name: String? = null,
    var description: String? = null,
    var urls: Map<String, Boolean>? = null,
    var isActive: Boolean,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null
)