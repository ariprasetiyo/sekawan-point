package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.CONFIG_TEST_MAX_LOP
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.SqlClient
import java.util.concurrent.ExecutorService

class RepositoryNonRxTesting(
    private val vertx: Vertx,
    private val vt: ExecutorService,
    private val sqlClient: SqlClient,
    private val gson: Gson,
    private val config: JsonObject
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
                if(true){
//                    return@submit findById()
                }
                logger.info("VT THREAD3: ${Thread.currentThread()}")
                return@submit "hasil dari thread ${Thread.currentThread()}"
            }.get()
        }, false)
            .onComplete { s, throwable ->
                logger.info(s)
                ctx.end(s)
            }

    }

    private fun findById(id: Long): Future<ArrayList<User>> {
        return sqlClient
            .query("SELECT username FROM ms_user")
            .execute().map {
                it.rowCount();
                val users = ArrayList<User>()
                for (row in it) {
                    val username = row.getString("username")
                    val user = User(userId = username)
                    users.add(user)

                }
                return@map users
            }
    }

}