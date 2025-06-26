package storage

import models.Rental
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

object RentalsDataMem {

    private val rentals   = mutableMapOf<Int, Rental>()
    private val idCounter = AtomicInteger(1)

    fun addRental(
        clubId: Int,
        courtId: Int,
        userId: Int,
        startTime: String,
        duration: Int
    ): Rental {
        require(clubId > 0)  { "Club ID must be greater than 0" }
        require(courtId > 0) { "Court ID must be greater than 0" }
        require(userId > 0)  { "User ID must be greater than 0" }

        require(ClubsDataMem.getClubById(clubId) != null)    { "Club ID '$clubId' not found" }
        require(CourtsDataMem.getCourtById(courtId) != null) { "Court ID '$courtId' not found" }
        require(UsersDataMem.getUserById(userId) != null)    { "User ID '$userId' not found" }

        require(duration in 1..10) { "Duration must be between 1 and 10 hours" }

        val parsed = try {
            LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid startTime format; must be ISO-8601", e)
        }

        require(parsed.hour in 8..17) { "Start time hour must be between 08 and 17" }

        val rentalId = idCounter.getAndIncrement()
        val rental = Rental(
            rentalId = rentalId,    // ✅ düzeltildi
            clubId   = clubId,
            courtId  = courtId,
            userId   = userId,
            startTime = startTime,
            duration = duration
        )
        rentals[rentalId] = rental
        return rental
    }

    fun getRentalById(rentalId: Int): Rental? {
        require(rentalId > 0) { "Rental ID must be greater than 0" }
        return rentals[rentalId]
    }

    fun getAllRentals(): List<Rental> = rentals.values.toList()

    fun getRentalsForClubAndCourt(
        clubId: Int,
        courtId: Int,
        date: String? = null
    ): List<Rental> = getAllRentals().filter { r ->
        r.clubId == clubId && r.courtId == courtId &&
                (date == null || r.startTime.startsWith(date))
    }

    fun getRentalsForUser(userId: Int): List<Rental> =
        getAllRentals().filter { it.userId == userId }

    fun getAvailableHours(clubId: Int, courtId: Int, date: String): List<Int> {
        val occupied = getRentalsForClubAndCourt(clubId, courtId, date).flatMap { r ->
            val start = LocalDateTime.parse(r.startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).hour
            (start until start + r.duration)
        }
        return (8..17).filterNot { it in occupied }
    }

    fun deleteRental(rentalId: Int): Boolean =
        rentals.remove(rentalId) != null

    fun updateRental(
        rentalId: Int,
        newStartTime: String,
        newDuration: Int,
        newCourtId: Int
    ): Rental {
        val existing = getRentalById(rentalId)
            ?: throw IllegalArgumentException("Rental ID '$rentalId' not found")

        deleteRental(rentalId)

        return addRental(
            clubId    = existing.clubId,
            courtId   = newCourtId,
            userId    = existing.userId,
            startTime = newStartTime,
            duration  = newDuration
        )
    }
}
