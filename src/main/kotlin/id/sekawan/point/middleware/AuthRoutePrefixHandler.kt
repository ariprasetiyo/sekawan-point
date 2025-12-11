package id.sekawan.point.middleware

import com.google.gson.Gson
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

class AuthRoutePrefixHandler(
    val gson: Gson,
    private val authRequiredHandler: AuthRequiredHandler
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this.javaClass.name)

    override fun handle(ctx: RoutingContext) {

        //http://localhost:8080/backoffice/v1/dashboard?username=admin
        val defaultAuthPrefixes = listOf(
            "/api/v1/", "/v1/admin/", "/backoffice/" , "/js",  "/vendor","/css"
        )

        val internalAuthPrefixes = listOf(
            "/internal/*"
        )

        // Perubahan ini dilakukan agar atribut `http.route` di OpenTelemetry (OTEL)
        // menampilkan nilai endpoint yang sesuai (misalnya: "/api/merchant/qr/product/create"),
        // bukan pola wildcard seperti "/api/merchant/*".
        val path = ctx.request().path().removeSuffix("/")
        when {
            defaultAuthPrefixes.any { path.startsWith(it) } -> authRequiredHandler.handle(ctx)
            internalAuthPrefixes.any { path.startsWith(it) } -> authRequiredHandler.handle(ctx)
            else -> ctx.next()
        }
    }
}