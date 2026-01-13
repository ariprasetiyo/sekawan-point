package id.sekawan.point.util.mymodel

import id.sekawan.point.type.RoleType
import java.time.OffsetDateTime

data class User(
    var userId : String?= null,
    var username: String? = null,
    var passwordHash: String? = null,
    var email: String? = null,
    var emailHash: String? = null,
    var phoneNumber: String? = null,
    var phoneNumberHash: String? = null,
    var role: RoleType? = null,
    var roleId: String? = null,
    var roleName: String? = null,
    var isActive: Boolean? = null,
    var createdAt: OffsetDateTime? = null,
    var updatedAt: OffsetDateTime? = null

)