package id.sekawan.point.database

import id.sekawan.point.util.mymodel.Subscription
import id.sekawan.point.util.mymodel.User
import rx.Observable

interface MasterDataStore {
    fun beginTransaction(): Observable<Boolean>
    fun commitTransaction(vararg allDepends: Observable<*>): Observable<Boolean>
    fun rollbackTransaction(vararg allDepends: Observable<*>): Observable<Boolean>
    fun getSatu(id: String): Observable<Subscription>
    fun insertRegistrationUser(user: User) : Observable<Int>
    fun getRegistrationUser()
    fun insertRegistrationRole()
    fun getRegistrationRole()

}
