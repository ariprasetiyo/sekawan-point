package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreRx
import id.sekawan.point.type.RequestType
import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import id.sekawan.point.util.mymodel.User
import id.sekawan.point.util.mymodel.UserRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class RegistrationUserDeleteHandler(
    private val masterDataStoreRx: MasterDataStoreRx,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String>(SESSION_USERNAME)

        Observable.just(ctx.body().asString())
            .observeOn(ioScheduler)
            .map { gson.fromJson(it!!, UserRequest::class.java) }
            .concatMap { request ->
                if (isValidRequest(request)) {

                    val user = User(
                        userId = request.body.userId!!,
                        username = request.body.username!!
                    )

                    return@concatMap masterDataStoreRx.deleteRegistrationUser(user)
                        .map { result ->
                            if (result > 0) {
                                return@map buildResponse(request, ResponseStatus.GENERAL_SUCCESS)
                            } else {
                                return@map buildResponse(request, ResponseStatus.GENERAL_FAILED)
                            }
                        }
                } else return@concatMap Observable.just(buildResponse(request, ResponseStatus.GENERAL_BAD_REQUEST))
            }
            .map { gson.toJson(it) }
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    ctx.response().end(t)
                }

                override fun onError(e: Throwable) {
//                    super.onError(e)
                    logger.error("requestError", e.message, e)
                    if (e !is HttpException) {
                        ctx.put("error", "${HttpResponseStatus.INTERNAL_SERVER_ERROR.code()} ${e.message}")
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun buildResponse(request: UserRequest, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = request.requestId
        response.type = RequestType.TYPE_DELETE_USER
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

    private fun isValidRequest(request: UserRequest): Boolean {
        return (
                request.type == RequestType.TYPE_DELETE_USER
                        && !StringUtils.isBlank(request.body.username)
                        && !StringUtils.isBlank(request.body.userId)
                )

    }
}