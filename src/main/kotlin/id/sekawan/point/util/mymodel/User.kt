package id.sekawan.point.util.mymodel

data class User(
    var userId : String,
    var username: String? = null,
    var passwordHash: String? = null,
    var email: String? = null,
    var emailHash: String? = null,
    var phoneNumber: String? = null,
    var phoneNumberHash: String? = null,
    var roleId: String? = null,
    var isActive: Boolean? = null,
)