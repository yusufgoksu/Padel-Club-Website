package services

import data.database.*
import models.Rental

object RentalServices {

    fun addRental(
        clubId: Int,
        courtId: Int,
        userId: Int,
        startTime: String,
        duration: Int
    ): Rental {
        // ───── Kimlik kontrolleri ───────────────────────────────────────────
        require(ClubsDataDb.getClubDetails(clubId)  != null) { "Club ID '$clubId' not found" }
        require(CourtsDataDb.getCourt(courtId)      != null) { "Court ID '$courtId' not found" }
        require(UserDataDb.getUserDetails(userId)   != null) { "User ID '$userId' not found" }
        require(duration in 1..10) { "Duration must be between 1 and 10 hours" }

        // ───── Tarih-saat doğrulaması (ISO-8601 + 08-17) ────────────────────
        val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val startLdt  = try {
            java.time.LocalDateTime.parse(startTime, formatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid startTime format; must be ISO-8601", e)
        }
        require(startLdt.hour in 8..17) { "Start time must be between 08:00 and 17:00" }

        // ───── Örtüşme kontrolü (aynı court + aynı tarih) ───────────────────
        val dateOnly   = startTime.substring(0, 10)             // "YYYY-MM-DD"
        val startHour  = startLdt.hour
        val newRange   = startHour until (startHour + duration)

        val overlaps = RentalDataDb
            .getRentals(clubId, courtId, dateOnly)              // O günkü mevcut kiralamalar
            .any { existing ->
                val exStartHour = java.time.LocalDateTime
                    .parse(existing.startTime, formatter).hour
                val exRange = exStartHour until (exStartHour + existing.duration)
                exRange.any { it in newRange }                  // Saat aralıkları kesişiyor mu?
            }

        require(!overlaps) { "This court is already booked for the selected time." }

        // ───── DB'ye ekle ve sonucu döndür ──────────────────────────────────
        return RentalDataDb.createRental(
            clubId   = clubId,
            courtId  = courtId,
            userId   = userId,
            date     = startTime,
            duration = duration
        )
    }

    fun getRentalById(id: Int): Rental? =
        RentalDataDb.getRentalDetails(id)

    fun getAllRentals(): List<Rental> =
        UserDataDb.getAllUsers().flatMap {
            RentalDataDb.getUserRentals(it.userId)
        }

    fun getRentalsForUser(userId: Int): List<Rental> =
        RentalDataDb.getUserRentals(userId)

    fun getRentalsForCourt(clubId: Int, courtId: Int, date: String? = null): List<Rental> =
        RentalDataDb.getRentals(clubId, courtId, date)

    fun getAvailableHours(clubId: Int, courtId: Int, date: String): List<Int> =
        RentalDataDb.getAvailableRentalHours(clubId, courtId, date)
            .mapNotNull { it.takeLast(5).take(2).toIntOrNull() }

    fun updateRental(id: Int, newStartTime: String, newDuration: Int): Rental {
        if (!RentalDataDb.updateRental(id, newStartTime, newDuration))
            throw IllegalStateException("Rental update failed")
        return RentalDataDb.getRentalDetails(id)
            ?: error("Rental not found after update")
    }

    fun deleteRental(id: Int): Boolean =
        RentalDataDb.deleteRental(id)

    fun usersWithCountsByCourt(courtId: Int) =
        RentalDataDb.getUsersWithRentalCountsByCourt(courtId)

    fun courtsWithCountsByUser(userId: Int): List<Pair<Int, Int>> {
        UserServices.getUserById(userId) ?: throw IllegalArgumentException("User not found")
        return RentalDataDb.getCourtsWithRentalCountsByUser(userId)
    }
}
