package id.sekawan.point.util.mymodel

class UserSessionDTO {
    var requestId  : String? = null
    var user : String? = null
    var roles : List<String>? = emptyList()
    var token : TokenDTO = TokenDTO()
}