package id.sekawan.point.handler.test

import com.google.gson.Gson
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.User
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.rxjava3.sqlclient.SqlClient
import java.util.ArrayList

class RepositoryRxJava3SingleTesting(
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val sqlClient: SqlClient,
    private val gson: Gson
) :
    Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        logger.info("VT THREAD1: ${Thread.currentThread()}")
        Single.just(true)
            .observeOn(ioScheduler)
            .concatMap {
                return@concatMap findById(1)
            }
            .map {
                logger.info("VT THREAD1.1: ${Thread.currentThread()} ${gson.toJson(it)}")
                var a = 0
                for (i in 1..2000000000) {
                    a += i;
                }
                logger.info("VT THREAD2: ${Thread.currentThread()}")
                return@map "success"
            }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            //.subscribe(object : DisposableObserver<String>() {}
            .subscribe(object : DefaultSubscriberTesting<String>(this::class.java.simpleName, ctx) {

                override fun onSuccess(t: String) {
                    logger.info("VT THREAD3: ${Thread.currentThread()}")
                    logger.info("response: $t")
                    ctx.response()
                        .setStatusCode(200)
                        .end(t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        ctx.put("error", "Invalid username or password")
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })

    }

    private fun findById(id: Long): Single<ArrayList<User>> {
        return sqlClient
            .query("SELECT username FROM ms_user")
            .execute().map {
                it.rowCount();
                val users = ArrayList<User>()
                for (row in it) {
                    val username  = row.getString("username")
                    val user = User(username = username)
                    users.add(user)

                }
                return@map users
            }
    }

}