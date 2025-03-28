package services

import models.*
import storage.CourtsDataMem
import storage.UsersDataMem

object UserServices {

    fun addUser(name: String, email: String): User {
        val user = User(name = name, email = email)
        UsersDataMem.users[user.uid] = user
        return user
    }

    fun getUsers(): List<User> = UsersDataMem.users.values.toList()

    fun getUserById(uid: String): User? = UsersDataMem.users[uid]


}
