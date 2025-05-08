package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int,  // userId artık integer olacak ve kullanıcı tarafından sağlanacak
    val name: String,
    val email: String,
    val token: String = java.util.UUID.randomUUID().toString() // Token hala UUID ile oluşturulacak
) {
    init {
        // userId'nin geçerli bir değer olup olmadığını kontrol et
        require(userId > 0) { "userId must be greater than 0" }

        // Name boş olamaz
        require(name.isNotBlank()) { "Name cannot be blank" }

        // E-posta geçerli formatta olmalı
        require(email.isNotBlank() && email.contains("@")) { "Invalid email format" }
    }
}
