package models


import kotlinx.serialization.Serializable
@Serializable
data class CourtInput(
    val name: String,
    val clubName: String
) {
    init {
        require(name.isNotBlank()) { "Court name cannot be blank" }
        require(clubName.isNotBlank()) { "Club name cannot be blank" }
    }
}
