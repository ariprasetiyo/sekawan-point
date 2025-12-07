package id.sekawan.point.util

import rx.Observable
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
