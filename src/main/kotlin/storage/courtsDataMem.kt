package storage

import models.*
import java.util.*

object CourtsDataMem {
    val users = mutableMapOf<String, User>()
    val clubs = mutableMapOf<String, Club>()
    val courts = mutableMapOf<String, Court>()
    val rentals = mutableMapOf<String, Rental>()

    fun addUser(name: String, email: String): User {
        val user = User(uid = UUID.randomUUID().toString(), name = name, email = email)
        users[user.uid] = user
        return user
    }

    fun addClub(name: String, ownerId: String): Club {
        val club = Club(cid = UUID.randomUUID().toString(), name = name, ownerUid = ownerId)
        clubs[club.cid] = club
        return club
    }

    fun addCourt(name: String, clubId: String): Court {
        val court = Court(crid = UUID.randomUUID().toString(), name = name, clubId = clubId)
        courts[court.crid] = court
        return court
    }

    fun addRental(userId: String, courtId: String, startTime: String, duration: Int): Rental {
        require(courts.containsKey(courtId)) { "Court ID not found" }
        require(users.containsKey(userId)) { "User ID not found" }

        val rental = Rental(
            rid = UUID.randomUUID().toString(),
            clubId = courts[courtId]?.clubId ?: throw IllegalArgumentException("Court's club ID is missing"),
            courtId = courtId,
            userId = userId,
            startTime = startTime,
            duration = duration
        )
        rentals[rental.rid] = rental
        return rental
    }

    // Optional: Add methods to retrieve entities
    fun getUserById(uid: String): User? = users[uid]
    fun getClubById(cid: String): Club? = clubs[cid]
    fun getCourtById(crid: String): Court? = courts[crid]
    fun getRentalById(rid: String): Rental? = rentals[rid]

    // Optional: Add methods to list all entities
    fun getAllUsers(): List<User> = users.values.toList()
    fun getAllClubs(): List<Club> = clubs.values.toList()
    fun getAllCourts(): List<Court> = courts.values.toList()
    fun getAllRentals(): List<Rental> = rentals.values.toList()
}
