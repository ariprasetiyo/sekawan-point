package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient
import java.util.concurrent.ExecutorService

class VirtualThreadOrganicRepository(
    private val vt: ExecutorService,
    private val config: JsonObject,
    private val gson: Gson,
    private val poolPg: SqlClient
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        logger.info("VT THREAD1: ${Thread.currentThread()}")
        val promise = Promise.promise<ArrayList<User>>()
        vt.submit {
            logger.info("VT THREAD2.1: ${Thread.currentThread()}")
            var a: Long = 0
            for (i in 1..config.getLong(CONFIG_TEST_MAX_LOP)) {
                a += i;
            }
            logger.info("VT THREAD2.2: ${Thread.currentThread()}")
            try {
                findById(1)
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
                    }.onSuccess {
                        logger.info("VT THREAD2.4: ${Thread.currentThread()}")
                        promise.complete(it)
                    }
                // heavy blocking work
//                promise.complete(ArrayList<User>())
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