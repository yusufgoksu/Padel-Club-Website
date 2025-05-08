package services

import models.User
import storage.UsersDataMem
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object AuthService {

    // Basit in-memory token deposu: token -> userId
    private val tokenStore = ConcurrentHashMap<String, Int>()

    /**
     * Yeni bir token üretir ve kullanıcıya atar.
     * @throws IllegalArgumentException Eğer userId geçersizse
     */
    fun issueToken(userId: Int): String {
        require(userId > 0) { "User ID must be greater than 0" }
        require(UsersDataMem.getUserById(userId) != null) { "User ID '$userId' not found" }

        val token = UUID.randomUUID().toString()
        tokenStore[token] = userId
        return token
    }

    /**
     * Verilen token geçerliyse ilgili User nesnesini döner,
     * değilse null döner.
     */
    fun authenticate(token: String): User? {
        val userId = tokenStore[token] ?: return null
        return UsersDataMem.getUserById(userId)
    }
}
