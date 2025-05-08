package data.database

import models.User
import interfaces.IuserServices
import java.sql.SQLException

object UserDataDb : IuserServices {

    /**
     * Inserts a new user and returns the generated integer user_id.
     */
    override fun createUser(name: String, email: String): Int {
        val sql = """
            INSERT INTO users (name, email)
            VALUES (?, ?)
            RETURNING user_id;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setString(2, email)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            rs.getInt("user_id")
                        } else {
                            throw SQLException("User creation failed, no ID returned.")
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating user: ${e.message}", e)
        }
    }

    /**
     * Fetch a single user by its integer ID.
     */
    override fun getUserDetails(userId: Int): User? {
        val sql = "SELECT user_id, name, email FROM users WHERE user_id = ?;"
        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, userId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            User(
                                userId = rs.getInt("user_id"),
                                name   = rs.getString("name"),
                                email  = rs.getString("email")
                            )
                        } else {
                            null
                        }
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
        val sql = "SELECT user_id, name, email FROM users;"
        val list = mutableListOf<User>()
        try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list += User(
                                userId = rs.getInt("user_id"),
                                name   = rs.getString("name"),
                                email  = rs.getString("email")
                            )
                        }
                    }
                }
            }
            return list
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching all users: ${e.message}", e)
        }
    }
}
