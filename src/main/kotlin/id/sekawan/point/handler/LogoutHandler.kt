package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class LogoutHandler(
    adminList: List<String>,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val renderHandler: RenderHandler
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String>(SESSION_USERNAME)
        isValidRequest(username)
            .map {
                ctx.session().destroy()
                return@map true
            }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<Boolean>(this::class.java.simpleName, ctx) {
                override fun onNext(t: Boolean) {
                    renderHandler.exec(ctx, "login.html")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun isValidRequest(username: String?): Observable<Boolean> {
        return if (StringUtils.isNotEmpty(username)
        ) {
            Observable.just(true)
        } else throwErrorBadRequest()
    }
}