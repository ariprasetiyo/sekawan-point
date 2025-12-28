package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.WorkerExecutor
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient

class WorkerExecutorRepository(
    private val executor: WorkerExecutor,
    private val config: JsonObject,
    private val poolPg: SqlClient,
    private val gson: Gson
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        executor.executeBlocking({

            logger.info("VT THREAD2: ${Thread.currentThread()}")
            var a: Long = 0
            for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                a += i;
            }
            return@executeBlocking a
        }, false)
            .compose{
                logger.info("VT THREAD2.2: ${Thread.currentThread()}")
                return@compose findById(1)
                    .map { return@map "string" }
                    .flatMap { findById(1) }
                    .map { result ->
                        val user = User(
                            username = "nama",
                            passwordHash = "nama",
                            email = "nama",
                            emailHash = "nama",
                            phoneNumber = "nama",
                            phoneNumberHash = "nama",
                            roleId = "",
                            isActive = true
                        )
                        result.add(user)
                        logger.info("VT THREAD2.3: ${Thread.currentThread()}")
                        return@map result
                    }
            }.map {
                gson.toJson(it)
            }
            .onSuccess {
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                ctx.end(it)
            }
            .onFailure{error ->
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                ctx.fail(error)
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
                    logger.info("VT THREAD2.1: ${Thread.currentThread()}")
                    val username = row.getString("name")
                    val user = User(username = username)
                    users.add(user)

                }
                return@map users
            }
    }

}