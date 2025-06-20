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
        require(ClubsDataDb.getClubDetails(clubId) != null)
        require(CourtsDataDb.getCourt(courtId) != null)
        require(UserDataDb.getUserDetails(userId) != null)
        require(duration in 1..10)

        return RentalDataDb.createRental(
            clubId = clubId,
            courtId = courtId,
            userId = userId,
            date = startTime,
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
