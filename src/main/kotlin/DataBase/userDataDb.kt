package data.database

import DataBase.Database
import models.User
import java.sql.Connection
import java.sql.SQLException
import interfaces.IuserServices


object UserDataDb : IuserServices {

    // Function to create a new user
    override fun createUser(name: String, email: String): String {
        val query = "INSERT INTO users (name, email) VALUES (?, ?) RETURNING user_id;"
        return try {
            Database.getConnection().use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setString(1, name)
                    preparedStatement.setString(2, email)

                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            return resultSet.getString("user_Id")
                        }
                        throw SQLException("User creation failed, no ID obtained.")
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating user: ${e.message}", e)
        }
    }

    // Function to get user details by UID
    override fun getUserDetails(userId: Int): User? {
        val query = "SELECT * FROM users WHERE user_id = ?;"

        return try {
            Database.getConnection().use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setInt(1, userId)
                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            return User(
                                userId = resultSet.getInt("user_id").toString(),
                                name = resultSet.getString("name"),
                                email = resultSet.getString("email")
                            )
                        }
                        null
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching user details: ${e.message}", e)
        }
    }

    // Function to get all users
    override fun getAllUsers(): List<User> {
        val query = "SELECT * FROM users;"
        val users = mutableListOf<User>()

        return try {
            Database.getConnection().use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            users.add(
                                User(
                                    userId = resultSet.getInt("userId").toString(),
                                    name = resultSet.getString("name"),
                                    email = resultSet.getString("email")
                                )
                            )
                        }
                    }
                }
            }
            users
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching all users: ${e.message}", e)
        }
    }
}
