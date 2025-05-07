package DataBase

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
    private const val url = "jdbc:postgresql://localhost:5432/postgres"
    private const val user = "postgres"
    private const val pass = "Hg268644"

    fun getConnection(): Connection {
        return try {
            DriverManager.getConnection(url, user, pass)
        } catch (e: SQLException) {
            throw RuntimeException("Error getting database connection", e)
        }
    }
}