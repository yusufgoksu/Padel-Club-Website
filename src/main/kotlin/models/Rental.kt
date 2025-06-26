package models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Serializable
data class Rental(
    val rentalId: Int? = null,   // DB atayacak → nullable
    val clubId:  Int,
    val courtId: Int,
    val userId:  Int,
    val startTime: String,       // ISO-8601 → “2025-01-01T10:00:00” Veya “…00Z”
    val duration: Int            // saat, 1-10
) {

    /* --------- ilk doğrulamalar --------- */

    init {
        if (rentalId != null) require(rentalId > 0) { "Rental ID must be > 0" }
        require(clubId  > 0) { "Club ID must be > 0" }
        require(courtId > 0) { "Court ID must be > 0" }
        require(userId  > 0) { "User ID must be > 0" }
        require(duration in 1..10) { "Duration must be 1-10 hours" }

        // Tarih-saat biçimi ve 08-17 UTC aralığı
        val inst = startInstant()   // helper (aşağıda)
        val hourUtc = inst.atZone(ZoneOffset.UTC).hour
        require(hourUtc in 8..17) { "Start time must be between 08:00 and 17:00 UTC" }
    }

    /* --------- Yardımcı fonksiyonlar --------- */

    /** startTime’i kesinlikle bir Instant’a dönüştürür (Z ekleyerek). */
    private fun startInstant(): Instant =
        try {                     // 1) “Z” içeriyorsa doğrudan parse
            Instant.parse(startTime)
        } catch (_: DateTimeParseException) {    // 2) “Z” yoksa ekleyip parse
            val ldt = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ldt.toInstant(ZoneOffset.UTC)
        }

    /** Kiralamanın bittiği UTC zamanı Instant olarak verir. */
    val endTime: Instant
        get() = startInstant().plusSeconds(duration * 3600L)
}
