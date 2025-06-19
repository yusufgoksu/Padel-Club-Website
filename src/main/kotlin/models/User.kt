package models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int , // sadece veritabanı tarafından atanır, kullanıcıdan gelmemeli
    val name: String,
    val email: String,
    val token: String = java.util.UUID.randomUUID().toString()
) {
    init {
        require(userId > 0) { "userId must be greater than 0 (assigned by DB)" }

        // ✅ İsim boş olmamalı
        require(name.isNotBlank()) {
            "Name cannot be blank"
        }

        // ✅ E-posta @ içermeli ve boş olmamalı
        require(email.isNotBlank() && email.contains("@")) {
            "Invalid email format"
        }
    }
}
