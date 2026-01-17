package id.sekawan.point.database

import id.sekawan.point.util.mymodel.Menu
import id.sekawan.point.util.mymodel.Role
import id.sekawan.point.util.mymodel.User
import io.reactivex.rxjava3.core.Observable

interface MasterDataStoreRx {

    fun insertRegistrationUser(user: User) : Observable<Int>
    fun deleteRegistrationUser(user: User) : Observable<Int>
    fun getRoles(): Observable<ArrayList<Role>>
    fun getUsers(): Observable<ArrayList<User>>
    fun getUserAuthByUsername(username: String, passwordHash : String): Observable<User>
    fun getUserDetails(userId : String): Observable<User>
    fun getMenus(roleId : String): Observable<ArrayList<Menu>>
    fun getAuthorizationRoles(): Observable<ArrayList<Role>>

}
