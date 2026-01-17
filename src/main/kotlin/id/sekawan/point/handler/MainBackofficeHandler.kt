package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreRx
import id.sekawan.point.type.RequestType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.Menu
import id.sekawan.point.util.mymodel.UserRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import org.apache.commons.lang3.StringUtils

class MainBackofficeHandler(
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val gson: Gson,
    private val masterDateStoreRx: MasterDataStoreRx,
    private val freeMakerEngine: FreeMarkerTemplateEngine,
    private val config: JsonObject
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String?>(SESSION_USERNAME)
        val roles = ctx.session().get<String>(SESSION_ROLE)

        Observable.just(1)
            .observeOn(ioScheduler)
            .map { isValidRequest(username, roles) }
            .flatMap { isValid ->
                if (isValid) {
                    return@flatMap masterDateStoreRx.getMenus(roles)
                }
                return@flatMap Observable.error(Throwable("haven't username $username or roles ${gson.toJson(roles)}"))
            }
            .map {
                return@map buildMenuTree(it)
            }
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<List<MenuNode>>(this::class.java.simpleName, ctx) {
                override fun onNext(t: List<MenuNode>) {

//                    printMenuTree(t)
                    val data = JsonObject()
                    data.put("username", username)
                    data.put("menus", t)

                    freeMakerEngine.render(data, "v-main.html")
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

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        ctx.put("error", "${HttpResponseStatus.INTERNAL_SERVER_ERROR.code()} ${e.message}")
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun buildMenuTree(menus: List<Menu>): List<MenuNode> {

        val map = mutableMapOf<Int, MenuNode>()
        // 1. Convert semua menu ke node
        menus.forEach { menu ->
            map[menu.id] = MenuNode(
                id = menu.id,
                name = menu.name,
                icon = menu.icon,
                url = menu.url
            )
        }

        val roots = mutableListOf<MenuNode>()
        // 2. Susun parent - child
        menus.forEach { menu ->

            val node = map[menu.id]!!
            if (menu.parent == null) {
                roots.add(node)
            } else {
                map[menu.parent]?.children?.add(node)
            }
        }

        return roots
    }

    data class MenuNode(
        val id: Int,
        val name: String?,
        val icon: String?,
        val url: String?,
        val children: MutableList<MenuNode> = mutableListOf()
    )

    private fun printMenuTree(menus: List<MenuNode>, level: Int = 0) {
        menus.forEach { menu ->
            println("${"    ".repeat(level)}- ${menu.name}")
            printMenuTree(menu.children, level + 1)
        }
    }

    private fun isValidRequest(username: String, role: String): Boolean {
        return (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(role))

    }
}