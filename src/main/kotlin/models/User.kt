package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val userId: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val token: String = UUID.randomUUID().toString()
) {
    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(email.isNotBlank() && email.contains("@")) { "Invalid email format" }
    }
}