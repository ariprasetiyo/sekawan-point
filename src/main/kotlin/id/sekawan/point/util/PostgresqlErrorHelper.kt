package id.sekawan.point.util

import org.postgresql.util.PSQLException

class PostgresqlErrorHelper {
    companion object {
        val ERR_FOREIGN_KEY = "23503"
        val ERR_DUPLICATE = "23505"
        fun isPostgresUniqueConstraintError(throwable: Throwable): Boolean {
            if (isPostgresError(throwable)) {
                val exception = throwable.cause as PSQLException
                return exception.getSQLState() == ERR_DUPLICATE
            }
            return false
        }

        fun isPostgresForeignKeyError(throwable: Throwable): Boolean {
            if (isPostgresError(throwable)) {
                val exception = throwable.cause as PSQLException
                return exception.getSQLState() == ERR_FOREIGN_KEY
            }
            return false
        }

        fun isPostgresError(throwable: Throwable): Boolean {
            val cause = throwable.cause
            return cause is PSQLException
        }
    }
}