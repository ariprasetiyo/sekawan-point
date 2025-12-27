package id.sekawan.point.handler.test

import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient
import java.util.concurrent.ExecutorService

class VirtualThreadExecutorServiceRepository(
    private val vertx: Vertx,
    private val vt: ExecutorService,
    private val poolPg: SqlClient,
    private val config : JsonObject
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        vertx.executeBlocking<String>({
            logger.info("VT THREAD2: ${Thread.currentThread()}")
            return@executeBlocking vt.submit<String> {
                var a : Long = 0
                for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                    a += i;
                }
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                return@submit "test"
            }.get()
        }, false)
            .onComplete { s, throwable ->
                logger.info("VT THREAD4: ${Thread.currentThread()}")
                ctx.end(s)
            }
    }

}