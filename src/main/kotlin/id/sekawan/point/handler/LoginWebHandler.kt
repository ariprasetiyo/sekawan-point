package id.sekawan.point.handler

import id.sekawan.point.util.RenderHandler
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class LoginWebHandler(
    private val renderHandler: RenderHandler,
    private val pathHTML: String
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        val username = ctx.session().get<String>(SESSION_USERNAME)
        val data = JsonObject()
        if (StringUtils.isNotBlank(username)) {
            data.put("username", username)
            ctx.response()
                .putHeader("Location", "/backoffice/v1?username=$username")
                .setStatusCode(302)
                .end()
        } else {
            data.put("username", username)
            renderHandler.exec(ctx, pathHTML, data)
        }
    }

}