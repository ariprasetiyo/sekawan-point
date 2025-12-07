package id.sekawan.point

import id.sekawan.point.util.mylog.LoggerFactory
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking
import java.io.File

private val logger = LoggerFactory().createLogger("main")
fun main(args: Array<String>) = runBlocking {

    val vertx = Vertx.vertx(VertxOptions())
    val configPath = args.firstOrNull() ?: "conf-local/config.json"
    val configJson = File(configPath).readText()
    val config = JsonObject(configJson)

    val deploymentId = vertx.deployVerticle(MainVerticle(),  DeploymentOptions().setConfig(config)).await()
    logger.info("✅ Verticle deployed: $deploymentId")

    // Graceful shutdown hook
    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("⚙️ Shutting down Vert.x...")
        runBlocking {
            vertx.close().await()
        }
        logger.info("✅ Vert.x stopped gracefully.")
    })
}