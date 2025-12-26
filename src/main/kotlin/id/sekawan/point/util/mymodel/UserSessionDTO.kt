package id.sekawan.point.util.mymodel

import id.sekawan.point.type.RoleType

class UserSessionDTO {
    var requestId  : String? = null
    var user : String? = null
    var roles : List<RoleType>? = emptyList()
    var token : TokenDTO = TokenDTO()
}