package id.sekawan.point.util

class HttpException(message: String, httpStatusCode: Int) : RuntimeException(message) {
    private var errorCode: Int = 500
    fun getErrorCode():Int { return errorCode }

    init {
        RuntimeException(message)
        errorCode = httpStatusCode
    }
}