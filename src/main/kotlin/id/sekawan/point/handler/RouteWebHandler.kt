package id.sekawan.point.handler

import id.sekawan.point.util.RenderHandler
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

class RouteWebHandler(
    private val renderHandler: RenderHandler,
    private val pathHTML: String
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        val username = ctx.session().get<String>(SESSION_USERNAME)
        val data = JsonObject()
        data.put("username", username)
        renderHandler.exec(ctx, pathHTML, data)
    }

}