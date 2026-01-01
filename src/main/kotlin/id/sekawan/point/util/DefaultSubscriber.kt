package id.sekawan.point.util

import id.sekawan.point.util.mylog.LoggerFactory
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.reactivex.rxjava3.internal.util.EndConsumerHelper
import io.vertx.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.atomic.AtomicReference

open class DefaultSubscriber<T : Any>(val response: String, val routingContext: RoutingContext?) : Observer<T>,
    Disposable {
    internal var logger = LoggerFactory().createLogger("DefaultSubscriber")
    private var requestTime = routingContext!!.get<DateTime>(KEY_RESPONSE_START_TIME)!!
    val upstream: AtomicReference<Disposable?> = AtomicReference<Disposable?>()


    override fun onNext(t: T) {
//        logger.info("Response $t")
    }

    override fun onError(e: Throwable) {
        processBeforeSendError(e)
        routingContext?.response()?.setStatusCode(getErrorCode(e))?.end()
    }

    override fun onComplete() {
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

    private fun processBeforeSendError(e: Throwable) {
        logger.error("requestError", createErrorMessage(e.message), e)
    }

    private fun createErrorMessage(message: String?): String {
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
    }

    override fun onSubscribe(d: Disposable) {
        if (EndConsumerHelper.setOnce(this.upstream, d, this.javaClass)) {
            this.onStart()
        }
    }

    protected fun onStart() {
    }

    override fun isDisposed(): Boolean {
        return upstream.get() === DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(this.upstream)
    }
}