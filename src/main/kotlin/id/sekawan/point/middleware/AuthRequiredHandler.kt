package id.sekawan.point.middleware

import com.google.gson.Gson
import id.sekawan.point.type.ErrorLoginType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.HEADER_REQUEST_ID
import id.sekawan.point.util.JWT_SUB
import id.sekawan.point.util.SESSION_LOGIN
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.UserSessionDTO
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.authentication.TokenCredentials
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import org.apache.commons.lang3.StringUtils

class AuthRequiredHandler(
    private val jwtAuth: JWTAuth,
    val gson: Gson,
    private val freeMarkerEngine: FreeMarkerTemplateEngine,
    private val authorizationRoles: ArrayList<RoleType>,
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

        if (!authorizationRoles.any { it in roles }) {
            val authorizationRolesJson = gson.toJson(authorizationRoles)
            logger.warn("${dataSession.user} : invalid authorization / forbidden. authorization roles = $authorizationRolesJson,  dataSession $dataSession")
            renderForbidden(ctx, ErrorLoginType.FORBIDDEN)
            return
        }

        val credentials = TokenCredentials()
        credentials.token = dataSession.token.accessToken
        credentials.scopes = arrayListOf(dataSession.token.tokenType)

        jwtAuth.authenticate(credentials)
            .onSuccess { user ->

                val requestIdHeader = ctx.request().getHeader(HEADER_REQUEST_ID)
                if (StringUtils.isBlank(requestIdHeader)) {
                    val errorCode = ErrorLoginType.INVALID_REQUEST_ID_104
                    logger.warn(" ${dataSession.user} : invalid request id in body ${errorCode.errorCode}")
                    renderForbidden(ctx, errorCode)
                    return@onSuccess
                }

                if (ctx.request().method() == HttpMethod.POST) {
                    val requestIdBody = ctx.body().asJsonObject().getString("requestId")
                    if (requestIdHeader != requestIdBody) {
                        val errorCode = ErrorLoginType.INVALID_REQUEST_ID_105
                        logger.warn(" ${dataSession.user} : invalid request id header vs body ${errorCode.errorCode}")
                        renderForbidden(ctx, errorCode)
                        return@onSuccess
                    }
                }

                val username = user.get<String>(JWT_SUB)
                logger.info("$username : success login session & jwt")
                ctx.session().put(SESSION_USERNAME, dataSession.user)
                ctx.next()
            }
            .onFailure { err ->
                logger.warn(" ${dataSession.user} : invalid authentication jwt token", err.message, err)
                renderForbidden(ctx, ErrorLoginType.INVALID_AUTHENTICATION_103)
            }
    }

    private fun renderForbidden(ctx: RoutingContext, errorLoginType: ErrorLoginType) {
        val data = JsonObject().apply {
            put("errorCode", errorLoginType.errorCode)
            put("errorMessage", errorLoginType.errorMessage)
        }
        freeMarkerEngine.render(data, "forbidden.html").onSuccess { res ->
            ctx.response()
                .putHeader("Content-Type", "text/html; charset=UTF-8")
                .end(res)
        }.onFailure { res ->
            ctx.fail(res)
        }
    }
}