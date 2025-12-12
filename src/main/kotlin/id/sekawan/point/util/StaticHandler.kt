package id.sekawan.point.util

import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler

/**
 * this function render static resource likes js, css, etc
 */
class StaticHandler(private val config: JsonObject) {

    fun exec(pathUrl: String): Handler<RoutingContext> {
        return if (config.getBoolean(CONFIG_IS_ACTIVE_WEB_LOCAL_CACHE)) {
            StaticHandler.create(pathUrl)
                .setCachingEnabled(true)
                .setMaxAgeSeconds(60 * 60 * 24 * 30) // 30 days
        } else {
            StaticHandler.create(pathUrl)
                .setCachingEnabled(false)
        }

    }
}