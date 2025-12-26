package id.sekawan.point.database

import com.google.gson.Gson
import id.sekawan.point.util.mylog.LoggerFactory
import rx.Observable

class MasterTransactionDataStore(
    private val firstDataStore: MasterDataStore,
    val gson: Gson) {

    private val logger = LoggerFactory().createLogger(this.javaClass.simpleName)

    fun testSubscribe(
    ): Observable<String>? {
        // Begin transaction
        return firstDataStore.beginTransaction()
           .flatMap {
               firstDataStore.commitTransaction()
                return@flatMap Observable.just("sas")
            }
            .onErrorResumeNext { e ->
                logger.error("merchant subscribe", e.message, e)
                // Rollback the transaction if it was started but an error occurred
                firstDataStore.rollbackTransaction()
                    .flatMap { Observable.error<String>(e) }
            }
    }
}