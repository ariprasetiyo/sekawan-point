package id.sekawan.point

import com.github.davidmoten.rx.jdbc.Database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import freemarker.cache.FileTemplateLoader
import id.sekawan.point.database.MasterDataStoreImpl
import id.sekawan.point.database.MasterTransactionDataStore
import id.sekawan.point.handler.*
import id.sekawan.point.handler.test.*
import id.sekawan.point.middleware.AuthRequiredHandler
import id.sekawan.point.middleware.AuthRoutePrefixHandler
import id.sekawan.point.type.RoleType
import id.sekawan.point.util.*
import id.sekawan.point.util.mylib.GsonHelper
import id.sekawan.point.util.mylib.MyHash
import id.sekawan.point.util.mylog.LoggerFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.WorkerExecutor
import io.vertx.core.http.CookieSameSite
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.healthchecks.Status
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.healthchecks.HealthCheckHandler
import io.vertx.ext.web.sstore.cookie.CookieSessionStore
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import io.vertx.pgclient.PgBuilder
import io.vertx.pgclient.PgConnectOptions
import io.vertx.rxjava3.RxHelper
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlClient
import org.joda.time.DateTime
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainVerticle(val vertxRxJava3: io.vertx.rxjava3.core.Vertx) : AbstractVerticle() {

    private val logger = LoggerFactory().createLogger(this.javaClass.name)
    private lateinit var jwtAuth: JWTAuth
    private val pathResource = "resources/templates/backoffice/v1"
    private lateinit var executor: WorkerExecutor
    private lateinit var poolPgRxJava3: io.vertx.rxjava3.sqlclient.SqlClient
    private lateinit var poolPg: SqlClient
    private lateinit var poolJdbc: Database

    override fun start(startPromise: Promise<Void>) {

        poolJdbc = createSatuDatabase(config())
        poolPgRxJava3 = initDatabasePgRxJava3(config())
        poolPg = initDatabasePg(config())
        val router = createRouter()
        vertx.createHttpServer(createHttpServerOptions(config()))
            .requestHandler(router)
            .listen(config().getInteger(CONFIG_BIND_PORT))
            .onSuccess {
                logger.info("🚀 HTTP server running on port ${config().getInteger(CONFIG_BIND_PORT)}")
            }
            .onFailure { err ->
                logger.info("❌ Failed to start HTTP server: ${err.message}")
            }
    }

    override fun stop() {
        executor.close()
    }

    private fun createSatuDatabase(config: JsonObject): Database {
        return createDatabase(
            config.getString(CONFIG_SATU_DB_URL),
            config.getString(CONFIG_SATU_DB_USER),
            config.getString(CONFIG_SATU_DB_PASS),
            config.getInteger(CONFIG_SATU_DB_MIN_POOL),
            config.getInteger(CONFIG_SATU_DB_MAX_POOL),
            config.getLong(CONFIG_SATU_DB_CONNECT_TIMEOUT_MS),
            config.getLong(CONFIG_SATU_DB_LEAK_DETECTION_IN_MS)
        )
    }

    private fun initDatabasePgRxJava3(config: JsonObject): io.vertx.rxjava3.sqlclient.SqlClient {
        val connectOptions = PgConnectOptions()
            .setPort(config.getInteger(CONFIG_SATU_DB_PORT))
            .setHost(config.getString(CONFIG_SATU_DB_HOST))
            .setDatabase(config.getString(CONFIG_SATU_DB_NAME))
            .setUser(config.getString(CONFIG_SATU_DB_USER))
            .setPassword(config.getString(CONFIG_SATU_DB_PASS))

        val poolOptions = PoolOptions()
            .setMaxSize(config.getInteger(CONFIG_SATU_DB_MAX_POOL))
            .setShared(true)
//            .setMaxWaitQueueSize(1000)
            .setIdleTimeout(config.getInteger(CONFIG_SATU_DB_IDLE_TIMEOUT_MS))
            .setIdleTimeoutUnit(TimeUnit.MILLISECONDS)

        return io.vertx.rxjava3.pgclient.PgBuilder.pool()
            .with(poolOptions)
            .connectingTo(connectOptions)
//            .connectingTo(config.getString(CONFIG_SATU_DB_URL))
            .using(vertxRxJava3)
            .build()
    }

    private fun initDatabasePg(config: JsonObject): SqlClient {
        val connectOptions = PgConnectOptions()
            .setPort(config.getInteger(CONFIG_SATU_DB_PORT))
            .setHost(config.getString(CONFIG_SATU_DB_HOST))
            .setDatabase(config.getString(CONFIG_SATU_DB_NAME))
            .setUser(config.getString(CONFIG_SATU_DB_USER))
            .setPassword(config.getString(CONFIG_SATU_DB_PASS))

        val poolOptions = PoolOptions()
            .setMaxSize(config.getInteger(CONFIG_SATU_DB_MAX_POOL))
            .setMaxWaitQueueSize(1000)
            .setIdleTimeout(config.getInteger(CONFIG_SATU_DB_IDLE_TIMEOUT_MS))
            .setIdleTimeoutUnit(TimeUnit.MILLISECONDS)

        return PgBuilder.pool()
            .with(poolOptions)
            .connectingTo(connectOptions)
//            .connectingTo(config.getString(CONFIG_SATU_DB_URL))
            .using(vertx)
            .build()
    }

    private fun createHttpServerOptions(configObject: JsonObject): HttpServerOptions {
        val serverOptions = HttpServerOptions()
            .setTcpKeepAlive(true)
            //kill request process if too long. better
            .setIdleTimeout(1200)
            .setIdleTimeoutUnit(TimeUnit.SECONDS)
            .setAcceptBacklog(1024)
            .setTcpKeepAlive(true)
//        serverOptions.port = configObject.getInteger(CONFIG_BIND_PORT)!!
        serverOptions.isSsl = false
        return serverOptions
    }

    private fun createDatabase(
        dbUrl: String,
        dbUser: String,
        dbPass: String,
        minIdle: Int,
        maxPoolSize: Int,
        connectTimeout: Long,
        leakDetectionInMs: Long
    ): Database {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPass
            minimumIdle = minIdle
            maximumPoolSize = maxPoolSize
            connectionTimeout = connectTimeout
            leakDetectionThreshold = leakDetectionInMs
        }

        val dataSource = if (hikariConfig.jdbcUrl != null)
            HikariDataSource(hikariConfig)
        else
            throw IllegalAccessException("No Database config, please check the config!")
        return Database.fromDataSource(dataSource)
    }

    private fun createRouter() = Router.router(vertx).apply {
        val gson = GsonHelper.createGson()
        val vertxScheduler = RxHelper.scheduler(vertx)
        val ioScheduler = Schedulers.io()
        val masterDatastore = MasterDataStoreImpl(poolJdbc, gson)
        val satuTransactionDataStore = MasterTransactionDataStore(masterDatastore, gson)
        val myHash = MyHash(config().getString(CONFIG_HASH_SALT))

        val healthCheckReadiness = HealthCheckHandler.create(vertx)
        val healthCheckLiveness = HealthCheckHandler.create(vertx)
        healthCheckLiveness.register("sekawan-point-health-check") { future -> future.complete(Status.OK()) }
        healthCheckReadiness.register("sekawan-point-database") { future -> isDatabaseOk(future, poolJdbc) }

        val freeMakerEngine = FreeMarkerTemplateEngine.create(vertx, ".html")
        unwrapFreemakerConfiguration(freeMakerEngine)
        val renderHandler = RenderHandler(config(), freeMakerEngine)
        val staticHandler = StaticHandler(config())

        val vt = Executors.newVirtualThreadPerTaskExecutor()
        executor = vertx.createSharedWorkerExecutor("my-worker-pool")

        val sessionHandler = sessionHandler()
        jwtAuth = jwtAuth()

        route().handler(
            CorsHandler.create()
                .addOrigin("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
        )

        route().handler { context ->
            context.put(KEY_RESPONSE_START_TIME, DateTime())
            context.next()
        }

        route().handler(sessionHandler)
        route().handler(BodyHandler.create())
        route().handler { context ->
            logIncomingRequestWithTraceId(context)
            context.put(KEY_RESPONSE_START_TIME, DateTime())
            context.next()
        }

        val authRequiredHandler =
            AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.ADMIN, RoleType.BASIC_USER))

        val authSuperAdminHandler =
            AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.SUPER_ADMIN))
        val authAdminHandler = AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.ADMIN))
        val authBasicHandler = AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.BASIC_USER))
        val authApprovalHandler = AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.APPROVAL))
        val authReadOnlyHandler = AuthRequiredHandler(jwtAuth, gson, freeMakerEngine, arrayListOf(RoleType.READ_ONLY))

        route().handler(AuthRoutePrefixHandler(gson, authRequiredHandler, authAdminHandler))

        route("/css/*").handler(staticHandler.exec("resources/templates/css"))
        route("/img/*").handler(staticHandler.exec("resources/templates/img"))
        route("/js/*").handler(staticHandler.exec("resources/templates/js"))
        route("/scss/*").handler(staticHandler.exec("resources/templates/scss"))
        route("/vendor/*").handler(staticHandler.exec("resources/templates/vendor"))
        route("/backoffice/v1/*").handler(StaticHandler.create(pathResource))

        get("/test/vertx/virtualThread/eventBus/organic").handler(VirtualThreadEventBus(vertx, vt, config()))
        get("/test/vertx/virtualThread/executorService/organic").handler(
            VirtualThreadExecutorService(
                vertx,
                vt,
                config()
            )
        )
        get("/test/vertx/virtualThread/executeBlocking/organic").handler(
            VirtualThreadExecuteBlocking(
                executor,
                vt,
                config()
            )
        )
        get("/test/vertx/virtualThread/executorService/repository").handler(
            VirtualThreadExecutorServiceRepository(
                vertx,
                vt,
                poolPg,
                config()
            )
        )
        get("/test/vertx/virtualThread/organic/repository").handler(
            VirtualThreadExecuteRepository(
                executor,
                vt,
                poolPg,
                gson,
                config()
            )
        )
        get("/test/vertx/rxJava3/organic").handler(VertxRxJava3Testing(vertxScheduler, ioScheduler, config()))
        get("/test/vertx/rxJava3/repository").handler(
            RepositoryRxJava3Testing(
                vertxScheduler,
                ioScheduler,
                poolPgRxJava3,
                gson,
                config()
            )
        )

        get("/login").handler(LoginWebHandler(renderHandler, "login.html"))
        post("/login").handler(
            LoginHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                renderHandler,
                jwtAuth,
                ArrayList()
            )
        )
        get("/backoffice/v1").handler(RouteWebHandler(renderHandler, "v-main.html"))
        post("/backoffice/v1").handler(
            DashboardHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                freeMakerEngine,
                ArrayList()
            )
        )
        get("/backoffice/v1/v-main").handler(RouteWebHandler(renderHandler, "v-main.html"))

        route("/api/v1/registration/*").handler(authSuperAdminHandler)
        post("/api/v1/registration/user/save").handler(
            RegistrationUserHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                myHash,
                ArrayList()
            )
        )
        post("/api/v1/registration/user/list").handler(
            DashboardHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                freeMakerEngine,
                ArrayList()
            )
        )
        post("/api/v1/registration/role/save").handler(
            DashboardHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                freeMakerEngine,
                ArrayList()
            )
        )
        post("/api/v1/registration/role/list").handler(
            DashboardHandler(
                masterDatastore,
                gson,
                vertxScheduler,
                ioScheduler,
                freeMakerEngine,
                ArrayList()
            )
        )

        get("/forbidden").handler(ForbiddenWebHandler(ArrayList(), renderHandler))
        get("/logout").handler(LogoutHandler(ArrayList(), gson, vertxScheduler, ioScheduler, renderHandler))
        get("/clear-cache").handler(ClearCachelHandler(ArrayList(), freeMakerEngine, gson, vertxScheduler, ioScheduler))

        get("/internal/v1/health/ready").handler(healthCheckReadiness)
        get("/internal/v1/health/live").handler(healthCheckLiveness)


        post("/api/v1/subscribe").handler(
            SatuTestHandler(
                masterDatastore, gson, vertxScheduler, ioScheduler, ArrayList()
            )
        )
    }

    private fun isDatabaseOk(future: Promise<Status>, satuDB: Database) {
        try {
            val conn = satuDB.connectionProvider.get()
            try {
                if (conn.isClosed) {
                    future.complete(Status.KO())
                } else {
                    future.complete(Status.OK())
                }
            } finally {
                conn.close()
            }
        } catch (ex: Exception) {
            logger.error("error db", ex.message, ex)
            future.complete(Status.KO())
        }
    }

    private fun logIncomingRequestWithTraceId(context: RoutingContext) {
        try {
            //val traceId = Span.current().spanContext.traceId
            val method = context.request().method()
            val url = context.request().absoluteURI()
            val body = context.body().asString()
            logger.info("Incoming request", "method=$method | url=$url | body=$body")
        } catch (ex: Exception) {
            logger.error("Failed to log incoming request", ex.message, ex)
            ex.printStackTrace()
        }
    }

    private fun unwrapFreemakerConfiguration(freeMakerEngine: FreeMarkerTemplateEngine) {
        // get the underlying FreeMarker Configuration
        val cfg = freeMakerEngine.unwrap()
        // Only proceed if unwrap() returns a valid Configuration
        if (cfg != null) {
            cfg.templateLoader = FileTemplateLoader(File(pathResource))
            cfg.defaultEncoding = "UTF-8"

            //cache in middleware
            if (config().getBoolean(CONFIG_IS_ACTIVE_FREEMAKER_CACHE)) {
                cfg.templateUpdateDelayMilliseconds = Long.MAX_VALUE    // check changes every 5 sec
                cfg.cacheStorage = freemarker.cache.MruCacheStorage(0, 250)  // default LRU cache
                logger.info("freeMaker cache is active")

                // optional: disable caching for hot reload
                // cfg.templateUpdateDelayMilliseconds = 0
                // cfg.cacheStorage = freemarker.cache.NullCacheStorage()
            }
        } else {
            throw IllegalStateException("Cannot unwrap FreeMarker configuration")
        }
    }

    private fun sessionHandler(): SessionHandler {
        // next could be change to SessionStore store = RedisSessionStore.create(vertx, redis);
        val sessionStore = CookieSessionStore.create(vertx, "abc")
        val sessionSetup = SessionHandler.create(sessionStore)
            .setSessionTimeout(10 * 60 * 1000)   // 30 menit idle timeout
            //in seconds. if not set will session remove when browser closed
            .setCookieMaxAge(30 * 60)
            .setNagHttps(false)
            .setCookieSecureFlag(true)
            .setCookieSameSite(CookieSameSite.LAX)
        //.setCookieHttpOnlyFlag(true)
        return sessionSetup
    }

    private fun jwtAuth(): JWTAuth {
        // Secret key for HMAC SHA256 (gunakan key yang kuat di production)
        val jwtSecret = config().getString(CONFIG_SECRET_JWT)
        val jwtConfig = JWTAuthOptions()
            .addPubSecKey(PubSecKeyOptions().setAlgorithm("HS256").setBuffer(jwtSecret))
        return JWTAuth.create(vertx, jwtConfig)
    }

}