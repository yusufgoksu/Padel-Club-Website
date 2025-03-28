package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Club(
    val cid: String = UUID.randomUUID().toString(),  // Club ID otomatik olarak oluşturulacak
    val name: String,  // Kulüp adı
    val ownerUid: String  // Sahip UID'si
) {
    init {
        // Club adı boş olamaz
        require(name.isNotBlank()) { "Club name cannot be blank" }

        // Sahip UID'si boş olamaz
        require(ownerUid.isNotBlank()) { "Owner ID cannot be blank" }
    }
}
