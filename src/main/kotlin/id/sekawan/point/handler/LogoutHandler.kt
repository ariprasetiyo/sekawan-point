package id.sekawan.point.handler

import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.ext.web.RoutingContext

class LogoutHandler(adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        ctx.session().destroy()

        ctx.json(
            mapOf(
                "status" to "logged_out"
            )
        )
    }
}