package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreRx
import id.sekawan.point.type.RequestType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.*
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

        val requestId = ctx.request().getHeader(HEADER_REQUEST_ID)
        Observable.just(requestId)
            .observeOn(ioScheduler)
            .concatMap { requestId ->
                return@concatMap masterDataStoreRx.getRoles()
                    .map { buildResponse(requestId, ResponseStatus.GENERAL_SUCCESS, it) }
            }
            .map { gson.toJson(it) }
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {
                override fun onNext(t: String) {
//                    super.onNext(t)
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

    private fun buildResponse(requestId: String, status: ResponseStatus, roles: List<Role>): DefaultResponseT<RoleResponseBody> {

        val response = DefaultResponseT<RoleResponseBody>()
        response.requestId = requestId
        response.type = RequestType.TYPE_ROLE
        response.status = status.code
        response.statusMessage = status.message
        response.body = RoleResponseBody(roles)

        return response
    }

}