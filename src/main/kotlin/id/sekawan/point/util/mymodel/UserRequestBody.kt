package id.sekawan.point.util.mymodel

import id.sekawan.point.type.SearchType

class UserRequestBody {
    var page : Int = 0
    var size : Int = 0
    var searchText : String? = null
    var searchType : SearchType? = null
}