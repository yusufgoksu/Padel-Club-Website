package data.database

import interfaces.IuserServices
import models.User
import java.sql.SQLException

object UserDataDb : IuserServices {

    /**
     * Inserts a new user and returns the generated userId.
     */
    override fun createUser(name: String, email: String): Int {
        val sql = """
            INSERT INTO users (name, email)
            VALUES (?, ?)
            RETURNING userId;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setString(2, email)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt("userId")
                        else throw SQLException("User creation failed, no ID returned.")
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating user: ${e.message}", e)
        }
    }

    /**
     * Fetch a single user by userId.
     */
    override fun getUserDetails(userId: Int): User? {
        val sql = "SELECT userId, name, email FROM users WHERE userId = ?;"

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, userId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            User(
                                userId = rs.getInt("userId"),
                                name   = rs.getString("name"),
                                email  = rs.getString("email")
                            )
                        } else null
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching user details: ${e.message}", e)
        }
    }

    /**
     * List all users.
     */
    override fun getAllUsers(): List<User> {
        val sql = "SELECT userId, name, email FROM users;"
        val list = mutableListOf<User>()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list += User(
                                userId = rs.getInt("userId"),
                                name   = rs.getString("name"),
                                email  = rs.getString("email")
                            )
                        }
                    }
                }
            }
            list
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching all users: ${e.message}", e)
        }
    }
}
