package id.sekawan.point.handler.test

import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.mylog.LoggerFactory
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

class VertxRxJava3Testing(
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val config: JsonObject
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        logger.info("VT THREAD1: ${Thread.currentThread()}")
        Observable.just(true)
            .observeOn(ioScheduler)
            .map {
                var a : Long = 0
                for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                    a += i;
                }
                logger.info("VT THREAD2: ${Thread.currentThread()}")
                return@map "success"
            }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            //.subscribe(object : DisposableObserver<String>() {}
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {

                override fun onNext(t: String) {
                    logger.info("VT THREAD3: ${Thread.currentThread()}")
                    logger.info("response: $t")
                    ctx.response()
                        .setStatusCode(200)
                        .end(t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        ctx.put("error", "Invalid username or password")
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })

    }

}