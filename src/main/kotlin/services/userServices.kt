package services

import models.User
import storage.UsersDataMem

object UserServices {

    // Kullanıcı ekleme fonksiyonu
    fun addUser(name: String, email: String): User {
        // Giriş doğrulamaları
        require(name.isNotBlank()) { "Name cannot be empty" }
        require(email.isNotBlank()) { "Email cannot be empty" }
        require("@" in email) { "Email must be valid" }

        // E-posta adresinin daha önce var olup olmadığını kontrol et
        require(UsersDataMem.users.values.none { it.email == email }) {
            "Email already exists"
        }

        // userId'yi her yeni kullanıcı için artan şekilde oluşturuyoruz
        val userId = UsersDataMem.idCounter.getAndIncrement()

        // Yeni kullanıcıyı oluştur
        val user = User(userId = userId, name = name, email = email)

        // Kullanıcıyı veritabanına ekle
        UsersDataMem.users[user.userId] = user
        return user
    }

    // Tüm kullanıcıları listeleme
    fun getAllUsers(): List<User> =
        UsersDataMem.getAllUsers()

    // Kullanıcıyı ID'ye göre getirme
    fun getUserById(userID: Int): User? {
        require(userID > 0) { "User ID must be greater than 0" }
        return UsersDataMem.getUserById(userID)
    }

    // Kullanıcıyı e-posta ile getirme
    fun getUserByEmail(email: String): User? {
        require(email.isNotBlank()) { "Email cannot be empty" }
        return UsersDataMem.getUserByEmail(email)
    }

    // Kullanıcıya ait token üretme
    fun generateUserToken(uid: Int): Pair<String, Int>? {
        require(uid > 0) { "User ID must be greater than 0" }
        return UsersDataMem.generateUserToken(uid)
    }
}