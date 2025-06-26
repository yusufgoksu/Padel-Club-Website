package models

import kotlinx.serialization.Serializable

@Serializable
data class Court(
    val courtId: Int? = null,  // null: henüz DB tarafından atanmadı
    val name: String,
    val clubId: Int
) {
    init {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(clubId > 0) { "Club ID must be greater than 0" }

        // Eğer courtID atanmışsa, 0 olmamalı
        if (courtId != null) {
            require(courtId > 0) { "Court ID must be greater than 0" }
        }
    }
}
