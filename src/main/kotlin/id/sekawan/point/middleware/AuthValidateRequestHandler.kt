package id.sekawan.point.middleware

import id.sekawan.point.type.ErrorLoginType
import id.sekawan.point.util.HEADER_IP
import id.sekawan.point.util.HEADER_REQUEST_ID
import id.sekawan.point.util.HEADER_USER_AGENT
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class AuthValidateRequestHandler(
    private val authRequiredHandler: AuthRequiredHandler,
    private val isValidateRequestId : Boolean,
    private val prefixUrls : List<String>,
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this.javaClass.name)

    override fun handle(ctx: RoutingContext) {

        // Perubahan ini dilakukan agar atribut `http.route` di OpenTelemetry (OTEL)
        // menampilkan nilai endpoint yang sesuai (misalnya: "/api/merchant/qr/product/create"),
        // bukan pola wildcard seperti "/api/merchant/*".
        val path = ctx.request().path().removeSuffix("/")
        when {
            prefixUrls.any { path.startsWith(it) } && !isValidateRequestId -> authRequiredHandler.handle(ctx)
            prefixUrls.any { path.startsWith(it) } && isValidateRequestId -> authenticationApi(ctx)
            else -> ctx.next()
        }
    }

    private fun authenticationApi(ctx: RoutingContext) {
        val username = ctx.session().get<String>(SESSION_USERNAME)
        val headerRequestId = ctx.request().getHeader(HEADER_REQUEST_ID)
        val headerIP = ctx.request().getHeader(HEADER_IP)
        val headerUserAgent = ctx.request().getHeader(HEADER_USER_AGENT)
        if (StringUtils.isBlank(headerRequestId) || StringUtils.isBlank(headerIP) || StringUtils.isBlank(headerUserAgent)) {
            val errorCode = ErrorLoginType.INVALID_REQUEST_ID_104
            logger.warn("$username : invalid request id in body ${errorCode.errorCode} $headerRequestId $headerIP $headerUserAgent")
            return
        }

        if (ctx.request().method() == HttpMethod.POST) {
            val requestIdBody = ctx.body().asJsonObject().getString("requestId")
            if (headerRequestId != requestIdBody) {
                val errorCode = ErrorLoginType.INVALID_REQUEST_ID_105
                logger.warn("invalid request id header vs body $username $headerRequestId vs $requestIdBody: ${errorCode.errorCode}")
                return
            }
        }

        authRequiredHandler.handle(ctx)
    }
}