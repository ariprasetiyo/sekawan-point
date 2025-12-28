package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import java.util.concurrent.ExecutorService

class VirtualThreadOrganic(
    private val vt: ExecutorService,
    private val config: JsonObject,
    private val gson: Gson
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        val promise = Promise.promise<ArrayList<User>>()
        vt.submit {
            try {
                logger.info("VT THREAD2.1: ${Thread.currentThread()}")
                var a: Long = 0
                for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                    a += i;
                }
                logger.info("VT THREAD2.2: ${Thread.currentThread()}")
                // heavy blocking work
                promise.complete(ArrayList<User>())
            } catch (e: Exception) {
                promise.fail(e)
            }
        }
        promise.future()
            .map {
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                return@map gson.toJson(it)
            }
            .onSuccess { result ->
                logger.info("VT THREAD4: ${Thread.currentThread()}")
                ctx.end(result)
            }.onFailure { error ->
                ctx.fail(error)
            }
    }
}