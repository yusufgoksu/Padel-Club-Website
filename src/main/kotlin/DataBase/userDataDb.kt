package data.database

import interfaces.IuserServices
import models.User
import java.sql.SQLException

object UserDataDb : IuserServices {

    /**
     * ✅ Kullanıcıyı manuel ID ile ekler
     */
    override fun createUser(userId: Int, name: String, email: String): Int {
        val sql = """
            INSERT INTO users (userId, name, email)
            VALUES (?, ?, ?);
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, userId)
                    stmt.setString(2, name)
                    stmt.setString(3, email)
                    val updatedRows = stmt.executeUpdate()
                    if (updatedRows == 0) throw SQLException("No rows inserted")
                    userId
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating user: ${e.message}", e)
        }
    }

    /**
     * ✅ Kullanıcıyı otomatik ID ile ekler (UserInput kullanımı için uygun)
     */
    override fun addUser(name: String, email: String): User {
        val sql = """
            INSERT INTO users (name, email)
            VALUES (?, ?)
            RETURNING userId, name, email;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setString(2, email)

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            User(
                                userId = rs.getInt("userId"),
                                name = rs.getString("name"),
                                email = rs.getString("email")
                            )
                        } else {
                            throw IllegalStateException("User insertion failed.")
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error adding user: ${e.message}", e)
        }
    }

    /**
     * Kullanıcıyı ID ile getirir
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
                                name = rs.getString("name"),
                                email = rs.getString("email")
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
     * Tüm kullanıcıları getirir
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
                                name = rs.getString("name"),
                                email = rs.getString("email")
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