package id.sekawan.point.handler

import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine

class ForbiddenWebHandler(
    adminList: List<String>,
    private val freeMarkerEngine: FreeMarkerTemplateEngine
) :
    AdminHandler<RoutingContext>(adminList) {
    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        val data = JsonObject()
        freeMarkerEngine.render(data, "templates/forbidden.ftl").onSuccess { res ->
            ctx.response().end(res)
        }.onFailure { res ->
            ctx.fail(res)
        }
    }
}