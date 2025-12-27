package id.sekawan.point.handler.test

import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.cpu.CpuCoreSensor
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import java.util.concurrent.ExecutorService

class VirtualThreadEventBus(
    private val vertx: Vertx,
    private val vt: ExecutorService,
    private val config: JsonObject
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        asyncEventBus()

        logger.info("The result0 is: ${Thread.currentThread()} ${CpuCoreSensor.availableProcessors()}")
        vertx.eventBus().request<String>("blocking.service", "")
            .map { yy ->
                logger.info("The result1 is: ${Thread.currentThread()} ${yy.body()}")
            }.onComplete { xx ->
                logger.info("The result2 is: ${Thread.currentThread()} $xx")
                ctx.end("response")
            }
    }

    private fun asyncEventBus() {
        vertx.eventBus().consumer<String>("blocking.service") { msg ->
            vt.submit {
                var a: Long = 0
                for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                    a += i;
                }
                logger.info("VT THREAD: ${Thread.currentThread()}")
                msg.reply("done")
            }
        }
    }

}