package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreRx
import id.sekawan.point.type.RequestType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HEADER_REQUEST_ID
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.*
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.ext.web.RoutingContext

class RegistrationUserDetailHandler(
    private val masterDataStoreRx: MasterDataStoreRx,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val requestId = ctx.request().getHeader(HEADER_REQUEST_ID)
        Observable.just(ctx.body().asString())
            .observeOn(ioScheduler)
            .map { gson.fromJson(it!!, UserRequest::class.java) }
            .concatMap { request ->
                if (isValidRequest(request)) {
                    return@concatMap masterDataStoreRx.getUserDetails(request.body.userId!!)
                        .map {
                            it.roleName  = RoleType.fromId(it.roleId)?.alias
                            return@map it
                        }
                        .map { buildResponse(requestId, ResponseStatus.GENERAL_SUCCESS, it) }
                }
                return@concatMap Observable.just(buildResponse(requestId, ResponseStatus.GENERAL_FAILED, User(userId = request.body.userId!! )))
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
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun isValidRequest(request: UserRequest): Boolean {
        return (request.type == RequestType.TYPE_USERS)
    }

    private fun buildResponse(requestId: String, status: ResponseStatus, user: User): DefaultResponseT<User> {

        val response = DefaultResponseT<User>()
        response.requestId = requestId
        response.type = RequestType.TYPE_USERS
        response.status = status.code
        response.statusMessage = status.message
        response.body = user

        return response
    }
}