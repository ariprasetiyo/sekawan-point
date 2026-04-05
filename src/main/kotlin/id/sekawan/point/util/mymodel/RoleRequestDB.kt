package id.sekawan.point.util.mymodel

import id.sekawan.point.type.SearchType

data class RoleRequestDB (
    var offset : Int ,
    var limit: Int ,
    var searchText : String?,
    var searchType: SearchType?
)