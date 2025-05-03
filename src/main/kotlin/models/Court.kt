package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Court(
    val courtID: String = UUID.randomUUID().toString(),
    val name: String,
    val clubId: String
) {
    init {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(clubId.isNotBlank()) { "Club ID cannot be empty" }
    }
}