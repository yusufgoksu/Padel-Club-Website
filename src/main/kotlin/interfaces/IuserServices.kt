package interfaces

import models.User

interface IuserServices {
    fun createUser(userId: Int, name: String, email: String): Int

    fun addUser(name: String, email: String): User

    fun getUserDetails(userId: Int): User?

    fun getAllUsers(): List<User>
}
