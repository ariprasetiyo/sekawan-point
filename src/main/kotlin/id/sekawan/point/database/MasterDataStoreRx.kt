package id.sekawan.point.database

import id.sekawan.point.util.mymodel.*
import io.reactivex.rxjava3.core.Observable

interface MasterDataStoreRx {

    fun insertRegistrationUser(user: User) : Observable<Int>
    fun deleteRegistrationUser(user: User) : Observable<Int>
    fun getRoles(): Observable<ArrayList<Role>>
    fun getUsers(userRequestDB: UserRequestDB): Observable<ArrayList<User>>
    fun getRoles(roleRequestDB: RoleRequestDB): Observable<ArrayList<Role>>
    fun getUsersOld(): Observable<ArrayList<User>>
    fun getUserAuthByUsername(username: String, passwordHash : String): Observable<User>
    fun getUserDetails(userId : String): Observable<User>
    fun getMenus(roleId: List<String>): Observable<ArrayList<Menu>>
    fun getAuthorizationRoles(): Observable<ArrayList<Role>>

}
