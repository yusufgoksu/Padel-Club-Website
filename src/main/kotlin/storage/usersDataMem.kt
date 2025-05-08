package storage

import models.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object UsersDataMem {

    val users = mutableMapOf<Int, User>()  // userId artık integer olacak
    val idCounter = AtomicInteger(1)  // Sıralı userId oluşturmak için AtomicInteger kullanıyoruz

    // Yeni bir kullanıcı ekleme
    fun addUser(name: String, email: String): User {
        // E-posta adresinin daha önce var olup olmadığını kontrol et
        if (users.values.any { it.email == email }) {
            throw IllegalArgumentException("Email already exists") // Aynı e-posta engelleniyor
        }

        // userId'yi her yeni kullanıcı için artan şekilde oluşturuyoruz
        val userId = idCounter.getAndIncrement()  // AtomicInteger ile sıralı userId oluşturuluyor
        val user = User(userId = userId, name = name, email = email)

        // Kullanıcıyı veritabanına ekle
        users[user.userId] = user
        return user
    }

    // Kullanıcıyı ID'ye göre getirme
    fun getUserById(userID: Int): User? = users[userID]

    // Tüm kullanıcıları listeleme
    fun getAllUsers(): List<User> = users.values.toList()

    // Kullanıcıyı e-posta ile arama
    fun getUserByEmail(email: String): User? = users.values.find { it.email == email }

    // Token üretme ve uid döndürme
    fun generateUserToken(userID: Int): Pair<String, Int>? {
        val user = getUserById(userID)
        return user?.let {
            // Basit bir token üretimi (örneğin UUID kullanılarak)
            val token = UUID.randomUUID().toString()
            Pair(token, it.userId) // token ve userId'yi döndürüyoruz
        }
    }
}
