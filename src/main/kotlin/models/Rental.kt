package models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.UUID

@Serializable
data class Rental(
    val rid: String = UUID.randomUUID().toString(),
    val clubId: String,
    val courtId: String,
    val userId: String,
    val startTime: String,
    val duration: Int
) {
    init {
        require(clubId.isNotBlank()) { "Club ID cannot be blank" }
        require(courtId.isNotBlank()) { "Court ID cannot be blank" }
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        require(duration in 1..24) { "Duration must be between 1-24 hours" }

        try {
            Instant.parse(startTime)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format, use ISO-8601")
        }
    }

    // Helper property to get the end time
    val endTime: Instant
        get() = Instant.parse(startTime).plusSeconds(duration * 3600L)
}
