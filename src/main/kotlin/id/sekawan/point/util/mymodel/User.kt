package id.sekawan.point.util.mymodel

data class User(
    var username: String,
    var passwordHash: String? = null,
    var email: String? = null,
    var emailHash: String? = null,
    var phoneNumber: String? = null,
    var phoneNumberHash: String? = null,
    var roleId: String? = null,
    var isActive: Boolean? = null,
)