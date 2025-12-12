package id.sekawan.point.handler

import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.RenderHandler
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine

class ForbiddenWebHandler(
    adminList: List<String>,
    private val renderHandler: RenderHandler
) :
    AdminHandler<RoutingContext>(adminList) {
    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        val data = JsonObject()
        renderHandler.exec(ctx, "forbidden.html")
    }
}