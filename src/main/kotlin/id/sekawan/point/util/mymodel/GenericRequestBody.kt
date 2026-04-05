package id.sekawan.point.util.mymodel

import com.google.gson.annotations.SerializedName
import id.sekawan.point.type.SearchType

class GenericRequestBody {
    var page : Int = 0
    var size : Int = 0
    var searchText : String? = null
    @SerializedName("searchType")
    var searchType : SearchType? = null
}