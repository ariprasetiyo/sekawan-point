package id.sekawan.point.handler.test

import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Semaphore


class VirtualThreadExecutorServiceRepository(
    private val vertx: Vertx,
    private val vt: ExecutorService,
    private val poolPg: SqlClient,
    private val config: JsonObject
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    private val limiter: Semaphore = Semaphore(2)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        runInVirtualThread(vertx) {
            Thread.sleep(100) // simulate blocking I/O
            "OK from virtual thread"
        }.onSuccess { result ->
            ctx.response().end(result);
        }.onFailure { err ->
            ctx.fail(err);
        }
    }

    fun <T> runInVirtualThread(vertx: Vertx, task: Callable<T>): Future<T> {
        val promise = Promise.promise<T>()

        vertx.executeBlocking({
            try {
                limiter.acquire()
                try {
                    val result = task.call()
                    promise.complete(result)
                } finally {
                    limiter.release()
                }
            } catch (e: Exception) {
                promise.fail(e)
            }
        }, false)

        return promise.future()
    }

    private fun findById(id: Long): Future<ArrayList<User>> {
        return poolPg
            .query("select name from ms_roles mr ")
            .execute()
            .map {
                it.rowCount();
                val users = ArrayList<User>()
                for (row in it) {
                    logger.info("VT THREAD1.1: ${Thread.currentThread()}")
                    val username = row.getString("name")
                    val user = User(username = username)
                    users.add(user)

                }
                return@map users
            }
    }

}