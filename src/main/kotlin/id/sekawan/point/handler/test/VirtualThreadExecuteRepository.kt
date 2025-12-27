package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient
import java.util.concurrent.ExecutorService

class VirtualThreadExecuteRepository(
    private val executor: WorkerExecutor,
    private val vt: ExecutorService,
    private val poolPg: SqlClient,
    private val gson: Gson,
    private val config : JsonObject
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        findById(1)
            .compose { users ->
                var a : Long = 0
                for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                    a += i;
                }
                val promise = Promise.promise<ArrayList<User>>()
                vt.submit {
                    try {
                        logger.info("VT THREAD2: ${Thread.currentThread()}")
                        // heavy blocking work
                        promise.complete(users)
                    } catch (e: Exception) {
                        promise.fail(e)
                    }
                }

                return@compose promise.future()
                // Must return Future<T>
//                return@compose Future.succeededFuture(users)
            }
            .onSuccess { users ->
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                ctx.json(users)
            }
            .onFailure {
                ctx.fail(it)
            }

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