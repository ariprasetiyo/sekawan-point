package id.sekawan.point.database

import com.github.davidmoten.rx.jdbc.Database
import com.google.gson.Gson
import id.sekawan.point.mapper.SubscriptionMapper
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.Subscription
import rx.Observable
import java.util.stream.Collectors

class FirstDataStoreImpl(private val satuDB: Database, private val gson: Gson) : FirstDataStore {

    private val logger = LoggerFactory().createLogger(this.javaClass.simpleName)

    private val subscriptionMapper = SubscriptionMapper()

    private fun getQuestionMark(list: List<Any>): String {
        return list
            .stream()
            .map { x -> "?" }
            .collect(Collectors.joining(","))
    }

    private fun getQuestionMark(list: List<Any>, customQuestionMark: String): String {
        return list
            .stream()
            .map { customQuestionMark }
            .collect(Collectors.joining(","))
    }
    
    override fun beginTransaction(): Observable<Boolean> {
        return satuDB.beginTransaction()
    }

    override fun commitTransaction(vararg allDepends: Observable<*>): Observable<Boolean> {
        return satuDB.commit(*allDepends)
    }

    override fun rollbackTransaction(vararg allDepends: Observable<*>): Observable<Boolean> {
        return satuDB.rollback()
    }

    override fun getSatu(id: String): Observable<Subscription> {
        val sql = """
            SELECT 1
        """.trimIndent()

        return satuDB.select(sql)
            .parameters(id)
            .get(subscriptionMapper)
            .firstOrDefault(null)
    }


}