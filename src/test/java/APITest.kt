import io.mockk.junit.MockKJUnit4Runner
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets
import java.util.*


@RunWith(MockKJUnit4Runner::class)
class APITest {

    private val isTest = false
    private var httpClient: HttpClient? = null

    @Before
    fun init() {
        if (isTest) {
            this.httpClient = HttpClients.createDefault()
        }
    }

    @Test
    fun concurrentlyProcessing() {
        if (isTest) {
            val concurrently = 100
            val totalRequest = 1000
            val threadAliveInMinute = 10L
            println("start")


            for (i in 0..concurrently) {
                println("start $i")
                val t1 = Thread(Runnable {
                    hitQrisRefundAsIssuer(totalRequest, "http://localhost:8080/ari")
                }).start()
            }

            println("end")
            Thread.sleep(1000 * 60 * threadAliveInMinute)
        }
    }

    private fun hitQrisRefundAsIssuer(totalRequest: Int, url: String) {
        for (i in 1..totalRequest) {
            try {
                httpClient = HttpClients.createDefault()
//                val httpPost = HttpPost(url)
//                val json = "{}";
//                httpPost.addHeader("Content-Type", "application/json")
//                httpPost.entity = StringEntity(json)
//                val response: HttpResponse = httpClient!!.execute(httpPost)
//                val responseBody: String = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
//                println("response $responseBody")

                val httpGet = HttpGet(url)
                val response: HttpResponse = httpClient!!.execute(httpGet)
                val responseBody: String = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
                println("response $responseBody")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}