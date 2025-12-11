package id.sekawan.point.util

import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import org.joda.time.Duration
import rx.Subscriber

open class DefaultSubscriber<T>(val response: String, val routingContext: RoutingContext?) : Subscriber<T>() {
    internal var logger = LoggerFactory().createLogger("DefaultSubscriber")
    private var requestTime = routingContext!!.get<DateTime>(KEY_RESPONSE_START_TIME)!!

    override fun onCompleted() {
        val responseTime = DateTime.now()
        val latencyThreshold = 60 * 1000 // in milliseconds
        val duration = Duration(requestTime, responseTime).millis

        if (duration > 0) {
            if (duration >= latencyThreshold) {
                logger.warn("WARNING-LATENCY endpoint $response : $duration milliseconds")
            } else {
                logger.info("LATENCY endpoint $response : $duration milliseconds")
            }
        }
    }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {
        processBeforeSendError(e)
        routingContext?.response()?.setStatusCode(getErrorCode(e))?.end()
    }

    private fun processBeforeSendError(e: Throwable) {
        logger.error("requestError", createErrorMessage(e.message), e)
    }

    private fun createErrorMessage(message : String?): String {
        return """"Subscriber failed : $response
            Request IN : ${routingContext?.request()?.absoluteURI()}
            Body IN : ${routingContext?.body()?.asString()}
            Message : $message
             """.trimMargin()
    }

    private fun getErrorCode(e: Throwable): Int {
        return if (e is HttpException) {
            e.getErrorCode()
        } else {
            HttpStatus.SC_INTERNAL_SERVER_ERROR
        }
    }}