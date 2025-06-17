package models


import kotlinx.serialization.Serializable


@Serializable
data class ClubInput(
    val name: String,
    val email: String  // userName yerine email kullanılacak
) {
    init {
        require(name.isNotBlank()) { "Club name cannot be blank" }
        require(email.isNotBlank()) { "Owner email cannot be blank" }
    }
}
