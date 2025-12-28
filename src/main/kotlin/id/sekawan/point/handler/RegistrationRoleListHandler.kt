package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreRx
import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.ext.web.RoutingContext

class RegistrationRoleListHandler(
    private val masterDataStoreRx: MasterDataStoreRx,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String>(SESSION_USERNAME)

        Observable.just(1)
            .observeOn(ioScheduler)
            .concatMap { request ->
                return@concatMap masterDataStoreRx.getRoles()
                    .map { result ->
                        if (result.isEmpty()) {
                            return@map buildResponse( ResponseStatus.GENERAL_SUCCESS)
                        } else {
                            return@map buildResponse( ResponseStatus.GENERAL_FAILED)
                        }
                    }

            }
            .map { gson.toJson(it) }
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    ctx.response().end(t)
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

    private fun buildResponse( status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = ""
        response.type = ""
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

}