package id.sekawan.point.middleware

import com.google.gson.Gson
import id.sekawan.point.type.ErrorLoginType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.AuthorizationUrls
import id.sekawan.point.util.mymodel.Role
import id.sekawan.point.util.mymodel.UserSessionDTO
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.authentication.TokenCredentials
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import org.apache.commons.lang3.StringUtils
import org.apache.http.Header

class AuthRequiredHandler(
    private val jwtAuth: JWTAuth,
    val gson: Gson,
    private val freeMarkerEngine: FreeMarkerTemplateEngine,
    private val authorizationRolesMap: Map<String, AuthorizationUrls>,
    private val authorizationRoles: ArrayList<RoleType>
) : Handler<RoutingContext> {

    private val logger = LoggerFactory().createLogger(this.javaClass.name)

    override fun handle(ctx: RoutingContext) {

        val session = ctx.session()
        val dataSessionJson = session.get<String>(SESSION_LOGIN)
        if (dataSessionJson == null) {
            logger.warn("data session cookies is null")
            renderForbidden(ctx, ErrorLoginType.INVALID_AUTHENTICATION_102)
            return
        }

        val dataSession = gson.fromJson(dataSessionJson, UserSessionDTO::class.java)
        val roles = dataSession.roles!!

        for (role in roles) {
            val authorizationRole = authorizationRolesMap[role.id]
            if (authorizationRole == null) {
                logger.warn("invalid authorization / forbidden 101. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
                return
            } else if (ctx.request().method() == HttpMethod.POST && authorizationRole.urlsPost == null){
                logger.warn("invalid authorization / forbidden 101.1. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
                return
            } else if (ctx.request().method() == HttpMethod.GET && authorizationRole.urlsGet == null){
                logger.warn("invalid authorization / forbidden 101.2. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
                return
            }

            val path = ctx.request().path().removeSuffix("/")
            if (ctx.request().method() == HttpMethod.POST && authorizationRole.urlsPost != null && !authorizationRole.urlsPost!!.any { path.startsWith(it) }) {
                logger.warn("invalid authorization / forbidden 102. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
                return
            } else if (ctx.request().method() == HttpMethod.GET && authorizationRole.urlsGet != null && !authorizationRole.urlsGet!!.any { path.startsWith(it) }) {
                logger.warn("invalid authorization / forbidden 103. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
                return
            }
        }

       /* if (!authorizationRoles.any { it in roles }) {
            //&& roleseSesion check ke cache utk dapatin URL
            val authorizationRolesJson = gson.toJson(authorizationRoles)
            logger.warn("invalid authorization / forbidden. authorization roles ${dataSession.user} = $authorizationRolesJson,  dataSession $dataSession")
            renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
            return
        }*/

        val credentials = TokenCredentials()
        credentials.token = dataSession.token.accessToken
        credentials.scopes = arrayListOf(dataSession.token.tokenType)

        jwtAuth.authenticate(credentials)
            .onSuccess { user ->
                logger.warn("login success ${dataSession.user} = ${gson.toJson(user)}")
                ctx.session().put(SESSION_USERNAME, dataSession.user)
                ctx.next()
            }
            .onFailure { err ->
                logger.warn("invalid authentication jwt token ${dataSession.user}", err.message, err)
                renderForbidden(ctx, ErrorLoginType.INVALID_AUTHENTICATION_103)
            }
    }

    private fun renderForbidden(ctx: RoutingContext, errorLoginType: ErrorLoginType) {
        val data = JsonObject().apply {
            put("errorCode", errorLoginType.errorCode)
            put("errorMessage", errorLoginType.errorMessage)
        }
        freeMarkerEngine.render(data, "forbidden.html").onSuccess { res ->
            ctx.session().destroy()
            ctx.response()
                .putHeader("Content-Type", "text/html; charset=UTF-8")
                .setStatusCode(403)
                .end(res)
        }.onFailure { res ->
            ctx.fail(res)
        }
    }
}