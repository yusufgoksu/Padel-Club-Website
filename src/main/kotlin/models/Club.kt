package models

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val clubID: Int,   // clubID sıralı integer olacak
    val name: String,  // Kulüp adı
    val userID: Int    // Sahip UID'si (integer)
) {
    init {
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }
        require(clubID > 0) { "Club ID must be greater than 0" }
    }
}