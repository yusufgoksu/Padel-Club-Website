package models

import kotlinx.serialization.Serializable

@Serializable
data class Court(
    val courtID: Int,  // courtID artık kullanıcı tarafından sağlanan bir değer olacak
    val name: String,
    val clubId: Int
) {
    init {
        // Court adı boş olamaz
        require(name.isNotBlank()) { "Court name cannot be empty" }

        // clubId geçerli bir integer olmalı
        require(clubId > 0) { "Club ID must be greater than 0" }

        // courtID geçerli bir integer olmalı
        require(courtID > 0) { "Court ID must be greater than 0" }
    }
}
