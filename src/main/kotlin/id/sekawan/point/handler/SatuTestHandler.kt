package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreImpl
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import id.sekawan.point.util.mymodel.SubscribeUnsubscribeRequest
import id.sekawan.point.util.throwErrorBadRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Observable

class SatuTestHandler(
    private val satuDatastore: MasterDataStoreImpl,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(event: RoutingContext) {

        val request = getRequestParam(event.body().asString())
        logger.info("log info ${event.body().asString()}")

        isValidRequest(request)
            .concatMap {
                return@concatMap performMerchantSubscribe(request!!)
            }
            .map { gson.toJson(it) }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, event) {
                override fun onNext(t: String) {
                    logger.info("response: $t")
                    event.response().putHeader("Content-Type", "application/json")
                        .setStatusCode(HttpResponseStatus.OK.code())
                        .end(t)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        event.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun performMerchantSubscribe(request: SubscribeUnsubscribeRequest): Observable<DefaultResponse> {
        val subscriptionId = request.body.subscriptionId
        val merchantId = request.body.merchantId

        return  Observable.just(buildResponse(request, ResponseStatus.GENERAL_NOT_FOUND))
        /*return satuDatastore.getSatu(subscriptionId)
            .flatMap { subscription ->
                    logger.info("not found", "subscription id not found ${gson.toJson(request)}")
                    return@flatMap Observable.just(buildResponse(request, ResponseStatus.GENERAL_NOT_FOUND))
            }
            .onErrorReturn { e ->
                logger.error("Error during merchant subscription", e.message, e)
                return@onErrorReturn buildResponse(request, ResponseStatus.GENERAL_FAILED)
            }*/
    }

    private fun buildResponse(request: SubscribeUnsubscribeRequest, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = request.requestId
        response.type = request.type
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

    private fun getRequestParam(request: String): SubscribeUnsubscribeRequest? {
        return try {
            gson.fromJson(request, SubscribeUnsubscribeRequest::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("cant parse request ", request, e)
            null
        }
    }

    private fun isValidRequest(request: SubscribeUnsubscribeRequest?): Observable<Boolean> {
        return if (request != null
            && StringUtils.isNotEmpty(request.requestId)
            && StringUtils.isNotEmpty(request.body.merchantId)
            && StringUtils.isNotEmpty(request.body.subscriptionId)
            && StringUtils.isNotEmpty(request.body.loginUser)
        ) { Observable.just(true)
        } else throwErrorBadRequest()
    }

}