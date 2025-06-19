package data.database

import interfaces.IuserServices
import models.User
import java.sql.SQLException

object UserDataDb : IuserServices {


    override fun createUser(name: String, email: String): User {
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
            throw RuntimeException("Error creating user: ${e.message}", e)
        }
    }


    /**
     * ✅ Kullanıcıyı otomatik ID ile ekler (UserInput kullanımı için uygun)
     */


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