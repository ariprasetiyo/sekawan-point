package id.sekawan.point.database

import id.sekawan.point.util.mymodel.Subscription
import rx.Observable

interface FirstDataStore {
    fun beginTransaction(): Observable<Boolean>
    fun commitTransaction(vararg allDepends: Observable<*>): Observable<Boolean>
    fun rollbackTransaction(vararg allDepends: Observable<*>): Observable<Boolean>
    fun getSatu(id: String): Observable<Subscription>

}
