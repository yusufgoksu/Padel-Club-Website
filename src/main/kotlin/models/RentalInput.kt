package models


import kotlinx.serialization.Serializable

@Serializable
data class RentalInput(
    val userName: String,
    val courtName: String,
    val clubName: String,
    val date: String, // yyyy-MM-dd
    val hour: String, // HH:mm
    val duration: Int
)
