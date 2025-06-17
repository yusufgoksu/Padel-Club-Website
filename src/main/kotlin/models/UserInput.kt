package models

import kotlinx.serialization.Serializable

@Serializable
data class UserInput(
    val name: String,
    val email: String
) {
    init {
        require(name.isNotBlank()) { "User name cannot be blank" }
        require(email.isNotBlank()) { "Email cannot be blank" }
    }
}

