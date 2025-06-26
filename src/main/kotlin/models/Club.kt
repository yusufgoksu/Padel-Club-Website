package models

import kotlinx.serialization.Serializable

@Serializable
data class Club(
    val clubID: Int? = null,   // null: DB atayacak
    val name: String,
    val userID: Int
) {
    init {
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }

        // Eğer clubID atanmışsa, 0 olmamalı
        if (clubID != null) {
            require(clubID > 0) { "Club ID must be greater than 0 if provided" }
        }
    }
}
