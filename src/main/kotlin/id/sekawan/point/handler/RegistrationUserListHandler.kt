package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreImpl
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import id.sekawan.point.util.mymodel.SubscribeUnsubscribeRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Observable
import io.vertx.ext.web.RoutingContext
import java.util.*

class RegistrationUserListHandler(
    private val satuDatastore: MasterDataStoreImpl,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String>(SESSION_USERNAME)
        isValidRequest()
            .map {
                val request = SubscribeUnsubscribeRequest()
                request.requestId = UUID.randomUUID().toString()
                request.type = "login"
                return@map buildResponse(request, ResponseStatus.GENERAL_SUCCESS)
            }
            .map { gson.toJson(it) }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {
                override fun onNext(t: String) {
                    logger.info("response: $t")
                    ctx.response().end("<h1>Welcome, $username !</h1>")
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

    private fun buildResponse(request: SubscribeUnsubscribeRequest, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = request.requestId
        response.type = request.type
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

    private fun isValidRequest(): Observable<Boolean> {
            return Observable.just(true)
    }
}