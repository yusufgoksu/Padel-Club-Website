package storage

import models.*
import java.util.*

object UsersDataMem {
    val users = mutableMapOf<String, User>()

    // Yeni bir kullanıcı ekleme
    fun addUser(name: String, email: String): User {
        val user = User(userID = UUID.randomUUID().toString(), name = name, email = email)
        users[user.userID] = user
        return user
    }

    // Kullanıcıyı ID'ye göre getirme
    fun getUserById(uid: String): User? = users[uid]

    // Tüm kullanıcıları listeleme
    fun getAllUsers(): List<User> = users.values.toList()

    // Kullanıcıyı e-posta ile arama (Opsiyonel)
    fun getUserByEmail(email: String): User? = users.values.find { it.email == email }

    // Token üretme ve uid döndürme
    fun generateUserToken(uid: String): Pair<String, String>? {
        val user = getUserById(uid)
        return user?.let {
            // Basit bir token üretimi (örneğin UUID kullanılarak)
            val token = UUID.randomUUID().toString()
            Pair(token, it.userID) // token ve uid'yi döndürüyoruz
        }
    }
}
