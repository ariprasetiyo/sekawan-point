package id.sekawan.point.handler.test

import id.sekawan.point.util.HttpException
import id.sekawan.point.util.KEY_RESPONSE_START_TIME
import id.sekawan.point.util.mylog.LoggerFactory
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.vertx.ext.web.RoutingContext
import org.apache.http.HttpStatus
import org.joda.time.DateTime
import java.util.concurrent.atomic.AtomicReference

open class DefaultSubscriberTesting<T : Any>(val response: String, val routingContext: RoutingContext?) : SingleObserver<T>,
    Disposable {
    internal var logger = LoggerFactory().createLogger(this.javaClass.simpleName)
    private var requestTime = routingContext!!.get<DateTime>(KEY_RESPONSE_START_TIME)!!
    val upstream: AtomicReference<Disposable?> = AtomicReference<Disposable?>()

    override fun onSubscribe(d: Disposable) {
        logger.info("this subscribe ${Thread.currentThread()}")
    }

    override fun onSuccess(t: T) {
        logger.info("this onSuccess ${Thread.currentThread()}")
    }

    override fun onError(e: Throwable) {
        processBeforeSendError(e)
        routingContext?.response()?.setStatusCode(getErrorCode(e))?.end()
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

    protected fun onStart() {
    }

    override fun isDisposed(): Boolean {
        return upstream.get() === DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(this.upstream)
    }
}