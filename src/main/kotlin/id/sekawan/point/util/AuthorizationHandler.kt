package id.sekawan.point.util

import io.vertx.core.Handler

abstract class AuthorizationHandler<RoutingContext>(open val authorizationList: List<String>) : Handler<RoutingContext> {

    fun isAllow(){

    }
}