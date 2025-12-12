package id.sekawan.point.util

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler

/**
 * this function render static resource likes js, css, etc
 */
class StaticHandler(private val  pathUrl : String)  {

    fun exec(): Handler<RoutingContext> {
        return StaticHandler.create(pathUrl)
            .setCachingEnabled(true)
            .setMaxAgeSeconds(60 * 60 * 24 * 30) // 30 days
    }
}