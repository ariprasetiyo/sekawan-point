package id.sekawan.point.middleware

import com.google.gson.Gson
import id.sekawan.point.type.ErrorLoginType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class AuthRoutePrefixHandler(
    val gson: Gson,
    private val authRequiredHandler: AuthRequiredHandler,
    private val authAdminHandler: AuthRequiredHandler
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this.javaClass.name)

    override fun handle(ctx: RoutingContext) {

        //http://localhost:8080/backoffice/v1/dashboard?username=admin
        val defaultAuthPrefixes = listOf(
            "/api/v1/", "/v1/admin/", "/backoffice/", "/js", "/vendor", "/css"
        )

        val authAdmin = listOf(
            "/api/v1/registration/role", "/api/v1/registration/user"
        )

        val internalAuthPrefixes = listOf(
            "/internal/*"
        )

        // Perubahan ini dilakukan agar atribut `http.route` di OpenTelemetry (OTEL)
        // menampilkan nilai endpoint yang sesuai (misalnya: "/api/merchant/qr/product/create"),
        // bukan pola wildcard seperti "/api/merchant/*".
        val path = ctx.request().path().removeSuffix("/")
        when {
            defaultAuthPrefixes.any { path.startsWith(it) } -> authAdminHandler.handle(ctx)
            internalAuthPrefixes.any { path.startsWith(it) } -> authenticationApi(ctx)
            authAdmin.any { path.startsWith(it) } -> authenticationApi(ctx)
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
                logger.warn("$username : invalid request id header vs body ${errorCode.errorCode}")
                return
            }
        }

        authRequiredHandler.handle(ctx)
    }
}