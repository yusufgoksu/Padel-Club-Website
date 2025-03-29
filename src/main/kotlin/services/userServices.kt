package services

import models.User
import storage.UsersDataMem

object UserServices {

    fun addUser(name: String, email: String): User {
        val user = User(name = name, email = email)
        UsersDataMem.users[user.userID] = user
        return user
    }

    fun getUsers(): List<User> = UsersDataMem.users.values.toList()

    fun getUserById(userID: String): User? = UsersDataMem.users[userID]

    // Kullanıcıya ait bir token ile kullanıcıyı doğrulama
    fun getUserByToken(token: String): User? {
        return UsersDataMem.users.values.find { it.token == token }
    }
}
