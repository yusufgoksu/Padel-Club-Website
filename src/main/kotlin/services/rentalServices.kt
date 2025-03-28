package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import java.util.UUID

object RentalServices {

    fun addRental(clubId: String, courtId: String, userId: String, startTime: String, duration: Int): Rental {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        require(CourtsDataMem.courts.containsKey(courtId)) { "Court ID not found" }
        require(UsersDataMem.users.containsKey(userId)) { "User ID not found" }

        val rental = Rental(clubId = clubId, courtId = courtId, userId = userId, startTime = startTime, duration = duration)
        RentalsDataMem.rentals[rental.rid] = rental
        return rental
    }

    fun getRentals(): List<Rental> = RentalsDataMem.rentals.values.toList()

    fun getRentalById(rid: String): Rental? = RentalsDataMem.rentals[rid]
}
