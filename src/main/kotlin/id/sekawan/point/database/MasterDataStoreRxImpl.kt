package id.sekawan.point.database

import com.google.gson.Gson
import id.sekawan.point.util.DateTimeHelper.Companion.offsetDateTimeJakarta
import id.sekawan.point.util.mylog.LoggerFactory
import id.sekawan.point.util.mymodel.Role
import id.sekawan.point.util.mymodel.User
import io.reactivex.rxjava3.core.Observable
import io.vertx.rxjava3.sqlclient.SqlClient
import io.vertx.rxjava3.sqlclient.Tuple
import java.time.ZoneOffset
import java.util.stream.Collectors


class MasterDataStoreRxImpl(private val sqlClient: SqlClient, private val gson: Gson) : MasterDataStoreRx {

    private val logger = LoggerFactory().createLogger(this.javaClass.simpleName)

    private val insertRegistrationUserQuery = """
            insert into ms_user (username,password_hash, email , email_hash, phone_number, phone_number_hash, role_id, is_active, created_at, updated_at)
            value($1, $2 , $3, $4, $5, $6, $7, $8, now(), now())
        """.trimIndent()
    private val getRolesQuery = """
        select id, name, description, authorizations, is_active , created_at, updated_at from ms_roles
    """.trimIndent()

    private fun getQuestionMark(list: List<Any>, customQuestionMark: String): String {
        return list
            .stream()
            .map { customQuestionMark }
            .collect(Collectors.joining(","))
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

        /**
         * val tuples = listOf(
         *     Tuple.tuple().addString("Alice").addString("alice@mail.com"),
         *     Tuple.tuple().addString("Bob").addString("bob@mail.com"),
         *     Tuple.tuple().addString("Charlie").addString("charlie@mail.com")
         * )
         */
        val tuples = listOf(
            Tuple.tuple().addString(user.username).addString(user.passwordHash).addString(user.email)
                .addString(user.emailHash).addString(user.phoneNumber).addString(user.phoneNumberHash)
                .addString(user.roleId).addBoolean(user.isActive)
        )

        return sqlClient
            .preparedQuery(insertRegistrationUserQuery)
            .executeBatch(tuples)
            .map {
                return@map it.rowCount()
            }.toObservable()
    }

    override fun getRoles(): Observable<ArrayList<Role>> {
        return sqlClient.preparedQuery(getRolesQuery)
            .execute()
            .map { rows ->
                val roles = ArrayList<Role>()
                for (row in rows) {
                    roles.add(
                        Role(
                            id = row.getString("id"),
                            name = row.getString("name"),
                            description = row.getString("description"),
                            authorization = row.getJsonObject("authorizations"),
                            isActive = row.getBoolean("is_active"),
                            createdAt = offsetDateTimeJakarta(row.getOffsetDateTime("created_at")),
                            updatedAt = offsetDateTimeJakarta(row.getOffsetDateTime("updated_at"))
                        )
                    )
                }
                return@map roles
            }.toObservable()

    }

}