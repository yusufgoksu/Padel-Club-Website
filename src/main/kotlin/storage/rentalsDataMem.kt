package storage
import java.time.Instant
import java.time.ZoneOffset
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import models.*
import storage.CourtsDataMem.courts
import storage.UsersDataMem.users

object RentalsDataMem {

    val rentals = mutableMapOf<String, Rental>()

    fun addRental(clubID: String, userId: String, courtId: String, startTime: String, duration: Int): Rental {
        require(users.containsKey(userId)) { "User ID not found" }
        require(courts.containsKey(courtId)) { "Court ID not found" }
        require(duration in 1..10) { "Rental duration must be between 1 and 10 hours" }

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsedStartTime = try {
            LocalDateTime.parse(startTime, formatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid start time format")
        }

        val hour = parsedStartTime.hour
        require(hour in 8..17) { "Start time must be between 08:00 and 17:00" }

        val rental = Rental(
            rentalID = UUID.randomUUID().toString(),
            clubId = clubID,
            courtId = courtId,
            userId = userId,
            startTime = startTime,
            duration = duration
        )

        rentals[rental.rentalID] = rental
        return rental
    }

    fun getRentalById(rentalID: String): Rental? = rentals[rentalID]

    fun getAllRentals(): List<Rental> = rentals.values.toList()

    fun getRentalsForClubAndCourt(clubID: String, courtID: String, date: String? = null): List<Rental> {
        val filtered = rentals.values.filter {
            it.clubId == clubID && it.courtId == courtID
        }

        return if (date != null) {
            filtered.filter { it.startTime.startsWith(date) }
        } else {
            filtered
        }
    }

    fun getRentalsForUser(userId: String): List<Rental> =
        rentals.values.filter { it.userId == userId }


    fun getAvailableHours(clubId: String, courtId: String, date: String): List<Int> {
        // List of all possible hours from 08:00 to 17:00
        val allHours = (8..17).toMutableList()

        // Filter the rentals for the given club, court, and date
        val reservedHours = rentals.values
            .filter { rental ->
                rental.clubId == clubId && rental.courtId == courtId && rental.startTime.startsWith(date)
            }
            .flatMap { rental ->
                // Convert rental start time to hour in UTC
                val startHour = Instant.parse(rental.startTime)
                    .atZone(ZoneOffset.UTC)
                    .hour

                // Create a range of occupied hours based on the rental's start hour and duration
                (startHour until (startHour + rental.duration)).toList()
            }

        // Remove reserved hours from all possible hours
        allHours.removeAll(reservedHours.toSet())

        // Return the list of available hours
        return allHours
    }


    fun deleteRental(rentalID: String): Boolean =
        rentals.remove(rentalID) != null

    fun updateRental(rid: String, newStartTime: String, newDuration: Int, newCourtId: String): Rental {
        val rental = getRentalById(rid) ?: throw IllegalArgumentException("Rental with ID $rid not found")

        require(courts.containsKey(newCourtId)) { "Court ID not found" }
        require(newDuration in 1..10) { "Rental duration must be between 1 and 10 hours" }

        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsedNewStart = try {
            LocalDateTime.parse(newStartTime, formatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid start time format")
        }

        val hour = parsedNewStart.hour
        require(hour in 8..17) { "Start time must be between 08:00 and 17:00" }

        val updatedRental = rental.copy(
            startTime = newStartTime,
            duration = newDuration,
            courtId = newCourtId
        )

        rentals[rid] = updatedRental
        return updatedRental
    }
}