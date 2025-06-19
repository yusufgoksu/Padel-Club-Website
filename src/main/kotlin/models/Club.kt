package models

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val clubID: Int? = null,   // Artık veritabanı tarafından atanacak
    val name: String,
    val userID: Int
) {
    init {
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }
    }
}
