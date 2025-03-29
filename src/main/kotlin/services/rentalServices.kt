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

    fun getRentals(): List<Rental> = RentalsDataMem.rentals.values.toList()

    fun getRentalById(rentalID: String): Rental? = RentalsDataMem.rentals[rentalID]
}
