package models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Serializable
data class Rental(
    val rentalID: Int,
    val clubId: Int,
    val courtId: Int,
    val userId: Int,
    val startTime: String,
    val duration: Int
) {
    init {
        require(rentalID > 0) { "Rental ID must be greater than 0" }
        require(clubId > 0)   { "Club ID must be greater than 0" }
        require(courtId > 0)  { "Court ID must be greater than 0" }
        require(userId > 0)   { "User ID must be greater than 0" }
        require(duration in 1..10) { "Duration must be between 1 and 10 hours" }

        // Try full UTC ISO first, then local date-time
        val instant = try {
            Instant.parse(startTime)
        } catch (_: DateTimeParseException) {
            try {
                LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneOffset.UTC)
                    .toInstant()
            } catch (_: DateTimeParseException) {
                throw IllegalArgumentException(
                    "Invalid startTime format;"
                )
            }
        }

        // Optionally enforce business hours:
        val hourUtc = instant.atZone(ZoneOffset.UTC).hour
        require(hourUtc in 8..17) { "Start time must be between 08:00 and 17:00" }
    }

    /** endTime as an Instant */
    val endTime: Instant
        get() = Instant.parse(startTime)
            .plusSeconds(duration * 3_600L)
}
