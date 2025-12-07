package id.sekawan.point.util

import io.vertx.core.Handler

abstract class AdminHandler<RoutingContext>(open val adminList: List<String>) : Handler<RoutingContext> {
    fun isAdminUser(user: String?): Boolean {
        return adminList.contains(user)
    }
}