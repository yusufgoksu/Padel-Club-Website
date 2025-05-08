package interfaces

import models.User
interface IuserServices {
    fun createUser(name: String, email: String): Int

    fun getUserDetails(userId: Int): User?

    fun getAllUsers(): List<User>
}