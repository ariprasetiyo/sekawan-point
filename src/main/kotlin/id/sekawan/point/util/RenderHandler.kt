package id.sekawan.point.util

import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine

/**
 * this function render html to FE
 */
class RenderHandler(
    private val config: JsonObject,
    private val freeMarkerEngine: FreeMarkerTemplateEngine,

    ) {

    fun exec(ctx: RoutingContext, fileHtmlRender: String) {
        exec(ctx, fileHtmlRender, JsonObject())
    }

    fun exec(ctx: RoutingContext, fileHtmlRender: String, data: JsonObject) {
        freeMarkerEngine.render(data, fileHtmlRender)
            .onSuccess { res ->
                if (config.getBoolean(CONFIG_IS_ACTIVE_WEB_LOCAL_CACHE)) {
                    ctx.response()
                        .putHeader("Content-Type", "text/html; charset=utf-8")
                        .putHeader("Cache-Control", "public, max-age=3600") // cache for 1 hour
//                    .putHeader("Expires", "Wed, 21 Oct 2025 07:28:00 GMT") // optional
                        .end(res)
                } else {
                    ctx.response()
                        .putHeader("Content-Type", "text/html; charset=utf-8")
                        .end(res)
                }
            }.onFailure { res ->
                ctx.fail(res)
            }
    }

    fun exec(ctx: RoutingContext, fileHtmlRender: String, data: Map<String, Any>) {
        freeMarkerEngine.render(data, fileHtmlRender)
            .onSuccess { res ->
                if (config.getBoolean(CONFIG_IS_ACTIVE_WEB_LOCAL_CACHE)) {
                    ctx.response()
                        .putHeader("Content-Type", "text/html; charset=utf-8")
                        .putHeader("Cache-Control", "public, max-age=3600") // cache for 1 hour
//                    .putHeader("Expires", "Wed, 21 Oct 2025 07:28:00 GMT") // optional
                        .end(res)
                } else {
                    ctx.response()
                        .putHeader("Content-Type", "text/html; charset=utf-8")
                        .end(res)
                }
            }.onFailure { res ->
                ctx.fail(res)
            }
    }
}