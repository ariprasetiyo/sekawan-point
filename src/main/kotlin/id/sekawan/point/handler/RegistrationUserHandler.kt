package id.sekawan.point.handler

import com.google.gson.Gson
import id.sekawan.point.database.MasterDataRxStore
import id.sekawan.point.database.MasterDataStoreImpl
import id.sekawan.point.util.AdminHandler
import id.sekawan.point.util.DefaultSubscriber
import id.sekawan.point.util.HttpException
import id.sekawan.point.util.SESSION_USERNAME
import id.sekawan.point.util.mylib.MyHash
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.*
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.vertx.ext.web.RoutingContext
import org.apache.commons.lang3.StringUtils

class RegistrationUserHandler(
    private val masterDataStoreRx: MasterDataRxStore,
    private val gson: Gson,
    private val vertxScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val myHash: MyHash,
    adminList: List<String>
) : AdminHandler<RoutingContext>(adminList) {

    private val logger = LoggerFactory().createLogger(this::class.simpleName)

    override fun handle(ctx: RoutingContext) {

        val username = ctx.session().get<String>(SESSION_USERNAME)

        Observable.just(ctx.body().asString())
            .map { gson.fromJson(it!!, UserRequest::class.java) }
            .concatMap { request ->
                if (isValidRequest(request.body)) {

                    val passwordHash = myHash.md5WithSalt(request.body.password!!)
                    val emailHash = myHash.md5WithSalt(request.body.email!!)
                    val phoneNumberHash = myHash.md5WithSalt(request.body.phoneNumber!!)
                    val user = User(
                        username = request.body.username!!,
                        passwordHash = passwordHash,
                        email = request.body.email!!,
                        emailHash = emailHash,
                        phoneNumber = request.body.phoneNumber,
                        phoneNumberHash = phoneNumberHash,
                        roleId = request.body.roleId,
                        isActive = true
                    )

                    return@concatMap masterDataStoreRx.insertRegistrationUser(user)
                        .map { result ->
                            if (result > 0) {
                                return@map buildResponse(request, ResponseStatus.GENERAL_SUCCESS)
                            } else {
                                return@map buildResponse(request, ResponseStatus.GENERAL_FAILED)
                            }
                        }.toObservable()
                } else return@concatMap Observable.just(buildResponse(request, ResponseStatus.GENERAL_BAD_REQUEST))
            }
            .map { gson.toJson(it) }
            .subscribeOn(ioScheduler)
            .observeOn(vertxScheduler)
            .subscribe(object : DefaultSubscriber<String>(this::class.java.simpleName, ctx) {
                override fun onNext(t: String) {
                    super.onNext(t)
                    ctx.response().end(t)
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

    private fun buildResponse(request: UserRequest, status: ResponseStatus): DefaultResponse {
        val response = DefaultResponse()
        response.requestId = request.requestId
        response.type = request.type
        response.status = status.code
        response.statusMessage = status.message

        return response
    }

    private fun isValidRequest(user: UserRequestBody): Boolean {
        return (!StringUtils.isBlank(user.username)
                && !StringUtils.isBlank(user.email)
                && !StringUtils.isBlank(user.password)
                && !StringUtils.isBlank(user.roleId)
                && !StringUtils.isBlank(user.phoneNumber))
    }
}