package id.sekawan.point.database

import id.sekawan.point.util.mymodel.User
import io.reactivex.rxjava3.core.Single

interface MasterDataRxStore {
    fun insertRegistrationUser(user: User) : Single<Int>

}
