package id.sekawan.point.handler.test

import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.WorkerExecutor
import io.vertx.ext.web.RoutingContext
import java.util.concurrent.ExecutorService

class VirtualThreadExecuteBlocking(private val executor: WorkerExecutor, private val vt: ExecutorService) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")

        executor.executeBlocking<String>({
            var a = 0
            for(i in 1 .. 2000000000){
                a += i;
            }
            logger.info("VT THREAD2: ${Thread.currentThread()}")
            return@executeBlocking vt.submit<String> {
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                return@submit "hasil dari thread ${Thread.currentThread()}"
            }.get()
        }, false)
            .onComplete { s, throwable ->
                logger.info(s)
                ctx.end(s)
            }
    }

}