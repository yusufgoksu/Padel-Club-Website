package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

object RentalServices {

    fun addRental(clubId: String, courtId: String, userId: String, startTime: String, duration: Int): Rental {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        require(CourtsDataMem.courts.containsKey(courtId)) { "Court ID not found" }
        require(UsersDataMem.users.containsKey(userId)) { "User ID not found" }

        val rental = Rental(clubId = clubId, courtId = courtId, userId = userId, startTime = startTime, duration = duration)
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
        rentalID: String ,
        newStartTime: String,
        newDuration: Int,
        newCourtId: String
    ): Rental {
        val rental = RentalsDataMem.getRentalById(rentalID)
            ?: throw IllegalArgumentException("Rental with ID $rentalID not found")

        // Court ID'yi doğrulamak için doğru storage'ı kullanıyoruz
        require(CourtsDataMem.courts.containsKey(newCourtId)) { "Court ID not found" }

        val updatedRental = rental.copy(
            startTime = newStartTime,
            duration = newDuration,
            courtId = newCourtId
        )

        RentalsDataMem.rentals[rentalID] = updatedRental
        return updatedRental
    }
}
