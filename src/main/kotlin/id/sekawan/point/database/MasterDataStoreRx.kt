package id.sekawan.point.database

import id.sekawan.point.util.mymodel.Role
import id.sekawan.point.util.mymodel.User
import io.reactivex.rxjava3.core.Observable

interface MasterDataStoreRx {
    fun insertRegistrationUser(user: User) : Observable<Int>
    fun getRoles(): Observable<ArrayList<Role>>
    fun getUsers(): Observable<ArrayList<User>>

}
