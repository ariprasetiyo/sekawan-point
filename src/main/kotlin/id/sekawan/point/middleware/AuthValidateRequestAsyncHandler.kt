package id.sekawan.point.middleware

import com.google.gson.Gson
import id.sekawan.point.type.ErrorLoginType
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.authentication.TokenCredentials
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import org.apache.commons.lang3.StringUtils

class AuthValidateRequestAsyncHandler(
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val freeMarkerEngine: FreeMarkerTemplateEngine,
    private val gson: Gson,
    private val jwtAuth: JWTAuth,
    private val authorizationRolesMap: Map<String, AuthorizationUrls>,
    private val prefixUrlsNonAPI: List<String>,
    private val prefixUrlsAPI: List<String>
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

        Observable.just(session)
            .observeOn(ioScheduler)
            .concatMap {
                val dataSession = gson.fromJson(dataSessionJson, UserSessionDTO::class.java)
                val roles = dataSession.roles!!

                val credentials = TokenCredentials()
                credentials.token = dataSession.token.accessToken
                credentials.scopes = arrayListOf(dataSession.token.tokenType)

                return@concatMap jwtAuth.authenticate(credentials).toObservable()
                    .map {
                        ctx.session().put(SESSION_USERNAME, dataSession.user)
                        ctx.session().put(SESSION_ROLE, dataSession.roles!![0].id)

                        // Perubahan ini dilakukan agar atribut `http.route` di OpenTelemetry (OTEL)
                        // menampilkan nilai endpoint yang sesuai (misalnya: "/api/merchant/qr/product/create"),
                        // bukan pola wildcard seperti "/api/merchant/*".
                        val path = ctx.request().path().removeSuffix("/")
                        return@map when {
                            prefixUrlsNonAPI.any { path.startsWith(it) } -> authenticationNonApi(ctx, roles, dataSession)
                            prefixUrlsAPI.any { path.startsWith(it) } -> authenticationApi(ctx, roles, dataSession)
                            else -> {
                                val errorLog = ErrorLoginType.INVALID_AUTHENTICATION_106
                                logger.warn("not allow url to access ${errorLog.errorCode} ${errorLog.errorMessage} ${dataSession.user}  $path")
                                return@map errorLog
                            }
                        }
                    }
            }
            .subscribe(object : DefaultSubscriber<ErrorLoginType>(this::class.java.simpleName, ctx) {
                override fun onNext(t: ErrorLoginType) {
                    if (t == ErrorLoginType.AUTHENTICATION_SUCCESS) {
                        ctx.next()
                    } else{
                        renderForbidden(ctx, t)
                    }
                }

                override fun onError(e: Throwable) {
                    renderForbidden(ctx, ErrorLoginType.INVALID_AUTHENTICATION_106)
                }
            })
    }

    private fun authenticationApi(ctx: RoutingContext, roles: List<RoleType>, dataSession: UserSessionDTO): ErrorLoginType {
        val username = ctx.session().get<String>(SESSION_USERNAME)
        val headerRequestId = ctx.request().getHeader(HEADER_REQUEST_ID)
        val remoteIpAddress = ctx.request().remoteAddress()

        val headerUserAgent = ctx.request().getHeader(HEADER_USER_AGENT)
        if (StringUtils.isBlank(headerRequestId) || StringUtils.isBlank(headerUserAgent)) {
            val errorCode = ErrorLoginType.INVALID_REQUEST_ID_104
            logger.warn("$username : invalid request id in body ${errorCode.errorCode} $headerRequestId $remoteIpAddress $headerUserAgent")
            return errorCode
        }

        if (ctx.request().method() == HttpMethod.POST) {
            val requestIdBody = ctx.body().asJsonObject().getString("requestId")
            if (headerRequestId != requestIdBody) {
                val errorCode = ErrorLoginType.INVALID_REQUEST_ID_105
                logger.warn("invalid request id header vs body $username $remoteIpAddress $headerRequestId vs $requestIdBody: ${errorCode.errorCode}")
                return errorCode
            }
        }

        return authenticationNonApi(ctx, roles, dataSession)
    }

    private fun authenticationNonApi(ctx: RoutingContext, roles: List<RoleType>, dataSession: UserSessionDTO): ErrorLoginType {

        for (role in roles) {
            val authorizationRole = authorizationRolesMap[role.id]
            if (authorizationRole == null) {
                logger.warn("invalid authorization / forbidden 101. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                return ErrorLoginType.FORBIDDEN
            } else if (ctx.request().method() == HttpMethod.POST && authorizationRole.urlsPost == null) {
                logger.warn("invalid authorization / forbidden 101.1. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                return ErrorLoginType.FORBIDDEN
            } else if (ctx.request().method() == HttpMethod.GET && authorizationRole.urlsGet == null) {
                logger.warn("invalid authorization / forbidden 101.2. authorization roles null ${dataSession.user} = ${role.id},  dataSession $dataSession")
                return ErrorLoginType.FORBIDDEN
            }

            val path = ctx.request().path().removeSuffix("/")
            if (ctx.request().method() == HttpMethod.POST && authorizationRole.urlsPost != null && !authorizationRole.urlsPost!!.any { path.startsWith(it) }) {
                logger.warn("invalid authorization / forbidden 102. authorization roles null $path ${dataSession.user} = ${role.id},  dataSession ${gson.toJson(dataSession)}")
                return ErrorLoginType.FORBIDDEN
            } else if (ctx.request().method() == HttpMethod.GET && authorizationRole.urlsGet != null && !authorizationRole.urlsGet!!.any { path.startsWith(it) }) {
                logger.warn("invalid authorization / forbidden 103. authorization roles null $path ${dataSession.user} = ${role.id},  dataSession ${gson.toJson(dataSession)}")
                return ErrorLoginType.FORBIDDEN
            }
        }

        return ErrorLoginType.AUTHENTICATION_SUCCESS
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

    private fun <T : Any> Future<T>.toObservable(): Observable<T> {
        return Observable.create { emitter ->
            this
                .onSuccess {
                    if (!emitter.isDisposed) {
                        emitter.onNext(it)
                        emitter.onComplete()
                    }
                }
                .onFailure {
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
    }
}