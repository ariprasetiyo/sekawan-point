package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import io.opentelemetry.api.trace.Span
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import rx.Observable
import rx.Scheduler

class ClearCachelHandler(
    adminList: List<String>,
    private val freeMakerEngine: FreeMarkerTemplateEngine,
    val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        val traceId = Span.current().spanContext.traceId

        Observable.just(true)
            .map {
                freeMakerEngine.clearCache()
                return@map buildResponse(traceId, ResponseStatus.GENERAL_SUCCESS)
            }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<DefaultResponse>(this::class.java.simpleName, ctx) {
                override fun onNext(t: DefaultResponse) {
                    val response = gson.toJson(t)
                    ctx.response()
                        .end(response)

                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        val buildResponse  = buildResponse(traceId, ResponseStatus.GENERAL_FAILED)
                        val response = gson.toJson(buildResponse)
                        ctx.response()
                            .end(response)
                    }
                }
            })
    }

    private fun buildResponse(requestId: String, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = requestId
        response.type = TYPE_CLEAR_CACHE
        response.status = status.code
        response.statusMessage = status.message

        return response
    }
}