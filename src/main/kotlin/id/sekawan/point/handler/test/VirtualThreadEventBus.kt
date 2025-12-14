package id.sekawan.point.handler.test

import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.impl.cpu.CpuCoreSensor
import io.vertx.ext.web.RoutingContext
import java.util.concurrent.ExecutorService

class VirtualThreadEventBus(private val vertx: Vertx, private val vt : ExecutorService) : Handler<RoutingContext> {

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
                var a = 0
                for(i in 1 .. 2000000000){
                    a += i;
                }
                logger.info("VT THREAD: ${Thread.currentThread()}")
                msg.reply("done")
            }
        }
    }

}