package id.sekawan.point.database

import com.github.davidmoten.rx.jdbc.Database
import com.google.gson.Gson
import id.sekawan.point.mapper.SubscriptionMapper
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.Subscription
import id.sekawan.point.util.mymodel.User
import rx.Observable
import java.util.stream.Collectors

class MasterDataStoreImpl(private val masterDB: Database, private val gson: Gson) : MasterDataStore {

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
        return masterDB.beginTransaction()
    }

    override fun commitTransaction(vararg allDepends: Observable<*>): Observable<Boolean> {
        return masterDB.commit(*allDepends)
    }

    override fun rollbackTransaction(vararg allDepends: Observable<*>): Observable<Boolean> {
        return masterDB.rollback()
    }

    override fun getSatu(id: String): Observable<Subscription> {
        val sql = """
            SELECT 1
        """.trimIndent()

        return masterDB.select(sql)
            .parameters(id)
            .get(subscriptionMapper)
            .firstOrDefault(null)
    }

    /**
     * user_id varchar(255) primary key,
     *     username varchar,
     *     password_hash varchar,
     *     email varchar,
     *     email_hash varchar,
     *     phone_number varchar,
     *     phone_number_hash varchar,
     *     role_id varchar,
     *     is_active  bool,
     *     created_at timestamp with time zone not null,
     *     updated_at timestamp with time zone not null,
     */
    override fun insertRegistrationUser(user: User): Observable<Int> {
        val query = """
            insert into ms_user (username,password_hash, email , email_hash, phone_number, phone_number_hash, role_id, is_active, created_at, updated_at)
            value(?, ?, ?, ?, ?, ?, ?, ?, now(), now())
        """.trimIndent()
        return masterDB
            .update(query)
            .parameters(user.username)
            .parameters(user.passwordHash)
            .parameters(user.email)
            .parameters(user.emailHash)
            .parameters(user.phoneNumber)
            .parameters(user.passwordHash)
            .parameters(user.roleId)
            .parameters(user.isActive)
            .count()
    }

    override fun getRegistrationUser() {
        TODO("Not yet implemented")
    }

    override fun insertRegistrationRole() {
        TODO("Not yet implemented")
    }

    override fun getRegistrationRole() {
        TODO("Not yet implemented")
    }


}