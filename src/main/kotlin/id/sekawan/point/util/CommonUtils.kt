package id.sekawan.point.util

import io.reactivex.rxjava3.core.Observable
import java.util.*

fun throwErrorBadRequest(): Observable<Boolean> {
    return Observable.error(HttpException(HTTP_ERROR_MESSAGE_INVALID_REQUEST, HTTP_ERROR_BAD_REQUEST_CODE))
}

fun getUserBasicAuth(authorization: String): String {
    val parts = authorization.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    val credentials = String(Base64.getDecoder().decode(parts[1])).split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    return credentials[0]
}

fun Throwable.rootCause(): Throwable {
    var cause: Throwable? = this
    while (cause?.cause != null) {
        cause = cause.cause
    }
    return cause ?: this
}

fun getTotalRecords(page: Int, size : Int, recordsSizeFromDb : Int): Int {
    var totalRecords = 100
    if(recordsSizeFromDb == 0){
        totalRecords = page * size
    } else if(recordsSizeFromDb <= size  ){
        totalRecords = (page + 1 ) * size
    }
    return totalRecords
}

fun getOffset(page : Int, size: Int) : Int{
    return ( page - 1 ) *  size
}