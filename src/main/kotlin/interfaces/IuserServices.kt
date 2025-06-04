package interfaces

import models.User
interface IuserServices {
    fun createUser(userId: Int, name: String, email: String): Int

    fun getUserDetails(userId: Int): User?

    fun getAllUsers(): List<User>
}