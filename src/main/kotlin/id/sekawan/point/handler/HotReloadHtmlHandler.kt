package id.sekawan.point.handler

import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine

class HotReloadHtmlHandler(
    adminList: List<String>, val freeMakerEngine: FreeMarkerTemplateEngine
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {
        freeMakerEngine.clearCache()
    }
}