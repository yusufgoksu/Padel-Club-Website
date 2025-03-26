package models

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class Club(
    val cid: String = UUID.randomUUID().toString(),
    val name: String,
    val ownerUid: String
) {
    init {
        require(name.isNotBlank()) { "Club name cannot be blank" }
        require(ownerUid.isNotBlank()) { "Owner ID cannot be blank" }
    }
}

