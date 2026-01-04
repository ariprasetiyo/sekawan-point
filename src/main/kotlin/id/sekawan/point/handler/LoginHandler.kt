package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataStoreImpl
import id.sekawan.point.type.JWTTokenType
import id.sekawan.point.type.RequestType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.DefaultResponse
import id.sekawan.point.util.mymodel.ResponseStatus
import id.sekawan.point.util.mymodel.TokenDTO
import id.sekawan.point.util.mymodel.UserSessionDTO
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.JWTOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class LoginHandler(
    private val satuDatastore: MasterDataStoreImpl,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val renderHandler: RenderHandler,
    private val jwtAuth: JWTAuth,
    private val config: JsonObject,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.request().getFormAttribute("username") ?: ""
        val password = ctx.request().getFormAttribute("password") ?: ""

        var isValidLogin = false
        val roles = ArrayList<RoleType>()
        if (username == "admin" && password == "admin") {
            roles.add(RoleType.ADMIN)
            roles.add(RoleType.BASIC_USER)
            isValidLogin = true
        } else if (username == "user" && password == "user") {
            roles.add(RoleType.BASIC_USER)
            isValidLogin = true
        } else if (username == "user1" && password == "user1") {
            roles.add(RoleType.BLOCK_USER)
            isValidLogin = true
        }

        Observable.just(isValidLogin)
            .map {

                val requestId = UUID.randomUUID().toString()
                if (!it) {
                    return@map buildResponse(requestId, ResponseStatus.GENERAL_FAILED)
                }

                val jwtClaimsAccess = jwtClaims(username, config.getLong(CONFIG_SECURITY_JWT_TOKEN_EXPIRED_IN_SECONDS)!!, JWTTokenType.ACCESS.alias)
                val accessToken = jwtAuth.generateToken(jwtClaimsAccess, JWTOptions().setAlgorithm("HS256"))

                val jwtClaimsRefresh = jwtClaims(username, config.getLong(CONFIG_SECURITY_JWT_TOKEN_REFRESH_EXPIRED_IN_SECONDS)!!, JWTTokenType.REFRESH.alias)
                val refreshToken = jwtAuth.generateToken(jwtClaimsRefresh, JWTOptions().setAlgorithm("HS256"))

                val tokenDto = TokenDTO()
                tokenDto.accessToken = accessToken
                tokenDto.refreshToken = refreshToken
                tokenDto.tokenType = "Bearer"

                val userSession = UserSessionDTO()
                userSession.user = username
                userSession.roles = roles
                userSession.requestId = requestId
                userSession.token = tokenDto

                ctx.session().put(SESSION_LOGIN, gson.toJson(userSession))
                return@map buildResponse(requestId, ResponseStatus.GENERAL_SUCCESS)
            }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<DefaultResponse>(this::class.java.simpleName, ctx) {
                override fun onNext(t: DefaultResponse) {
                    logger.info("response: ${gson.toJson(t)}")
                    if (t.status == ResponseStatus.GENERAL_SUCCESS.code) {
                        ctx.response()
                            .putHeader("Location", "/backoffice/v1?username=$username")
                            .setStatusCode(302)
                            .end()
                    } else {
                        ctx.put("error", "Invalid username or password")
                        renderHandler.exec(ctx, "login.html")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e !is HttpException) {
                        ctx.put("error", "Invalid username or password")
                        ctx.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end()
                    }
                }
            })
    }

    private fun buildResponse(requestId: String, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = requestId
        response.type = RequestType.TYPE_LOGIN
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

    private fun jwtClaims(username: String, expiredInSecond: Long, type: String): JsonObject {
        return JsonObject()
            .put(JWT_SUB, username)
            .put(JWT_EXP, Instant.now().plusSeconds(expiredInSecond).epochSecond)
            .put(JWT_TYPE, type)
    }

}