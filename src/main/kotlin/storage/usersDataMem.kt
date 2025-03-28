package storage

import models.*
import java.util.*

object UsersDataMem {
    val users = mutableMapOf<String, User>()


    fun addUser(name: String, email: String): User {
        val user = User(uid = UUID.randomUUID().toString(), name = name, email = email)
        users[user.uid] = user
        return user
    }


    // Optional: Add methods to retrieve entities
    fun getUserById(uid: String): User? = users[uid]


    // Optional: Add methods to list all entities
    fun getAllUsers(): List<User> = users.values.toList()

}
