package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import java.time.Instant
import java.time.ZoneOffset

object RentalServices {

    fun addRental(clubId: String, courtId: String, userId: String, startTime: String, duration: Int): Rental {
        require(UsersDataMem.users.containsKey(userId)) { "User ID $userId' not found" }
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID $clubId' not found" }
        require(CourtsDataMem.courts.containsKey(courtId)) { "Court ID $courtId' not found" }


        val rental = Rental(
            clubId = clubId,
            courtId = courtId,
            userId = userId,
            startTime = startTime,
            duration = duration
        )
        RentalsDataMem.rentals[rental.rentalID] = rental
        return rental
    }

    fun getRentalById(rentalID: String): Rental? {
        return RentalsDataMem.getRentalById(rentalID)
    }

    fun getRentals(): List<Rental> {
        return RentalsDataMem.getAllRentals()
    }

    fun getRentalsForClubAndCourt(clubId: String, courtId: String, date: String? = null): List<Rental> {
        return RentalsDataMem.getRentalsForClubAndCourt(clubId, courtId, date)
    }

    fun getRentalsForUser(userId: String): List<Rental> {
        return RentalsDataMem.getRentalsForUser(userId)
    }

    fun deleteRental(rentalID: String): Boolean {
        return RentalsDataMem.rentals.remove(rentalID) != null
    }

    fun updateRental(
        rentalID: String,
        newStartTime: String,
        newDuration: Int,
        newCourtId: String
    ): Rental {
        val rental = RentalsDataMem.getRentalById(rentalID)
            ?: throw IllegalArgumentException("Rental with ID $rentalID not found")

        require(CourtsDataMem.courts.containsKey(newCourtId)) { "Court ID not found" }

        val updatedRental = rental.copy(
            startTime = newStartTime,
            duration = newDuration,
            courtId = newCourtId
        )

        RentalsDataMem.rentals[rentalID] = updatedRental
        return updatedRental
    }

    fun getAvailableHours(clubId: String, courtId: String, date: String): List<Int> {
        val allHours = (8..17).toMutableList()

        val reservedHours = RentalsDataMem.rentals.values
            .filter { it.clubId == clubId && it.courtId == courtId && it.startTime.startsWith(date) }
            .flatMap { rental ->
                val startHour = Instant.parse(rental.startTime).atZone(ZoneOffset.UTC).hour
                (startHour until (startHour + rental.duration)).toList()
            }

        allHours.removeAll(reservedHours.toSet())
        return allHours
    }
}
