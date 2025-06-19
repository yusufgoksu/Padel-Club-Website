package services

import models.User
import data.database.UserDataDb

object UserServices {



    fun createUser(name: String, email: String): User {
        require(name.isNotBlank()) { "Name cannot be empty" }
        require(email.isNotBlank()) { "Email cannot be empty" }
        require("@" in email) { "Email must be valid" }

        val existing = getUserByEmail(email)
        require(existing == null) { "Email already exists" }

        return UserDataDb.createUser(name, email)
    }

    fun getAllUsers(): List<User> =
        UserDataDb.getAllUsers()

    fun getUserById(userID: Int): User? {
        require(userID > 0) { "User ID must be greater than 0" }
        return UserDataDb.getUserDetails(userID)
    }

    fun getUserByEmail(email: String): User? {
        require(email.isNotBlank()) { "Email cannot be empty" }
        return UserDataDb.getAllUsers().find { it.email == email }
    }

    // Token üretimi
    private val tokenStore = mutableMapOf<String, Int>()

    fun generateUserToken(userId: Int): Pair<String, Int>? {
        require(userId > 0) { "User ID must be greater than 0" }
        val user = getUserById(userId) ?: return null

        val token = java.util.UUID.randomUUID().toString()
        tokenStore[token] = userId
        return token to userId
    }

    fun getUserFromToken(token: String): User? {
        val uid = tokenStore[token] ?: return null
        return getUserById(uid)
    }
}