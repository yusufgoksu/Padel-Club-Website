package services

import models.*
import storage.CourtsDataMem
import java.util.UUID

object CourtServices {

    fun addUser(name: String, email: String): User {
        val user = User(name = name, email = email)
        CourtsDataMem.users[user.uid] = user
        return user
    }

    fun getUsers(): List<User> = CourtsDataMem.users.values.toList()

    fun getUser(uid: String): User? = CourtsDataMem.users[uid]  // ✅ Eklendi

    fun addClub(name: String, ownerUid: String): Club {
        require(CourtsDataMem.users.containsKey(ownerUid)) { "Owner UID not found" }
        val club = Club(name = name, ownerUid = ownerUid)
        CourtsDataMem.clubs[club.cid] = club
        return club
    }

    fun getClubs(): List<Club> = CourtsDataMem.clubs.values.toList()

    fun getClub(cid: String): Club? = CourtsDataMem.clubs[cid]  // ✅ Eklendi

    fun addCourt(name: String, clubId: String): Court {
        require(CourtsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        val court = Court(name = name, clubId = clubId)
        CourtsDataMem.courts[court.crid] = court
        return court
    }

    fun getCourts(): List<Court> = CourtsDataMem.courts.values.toList()

    fun getCourt(crid: String): Court? = CourtsDataMem.courts[crid]  // ✅ Eklendi

    fun addRental(clubId: String, courtId: String, userId: String, startTime: String, duration: Int): Rental {
        require(CourtsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        require(CourtsDataMem.courts.containsKey(courtId)) { "Court ID not found" }
        require(CourtsDataMem.users.containsKey(userId)) { "User ID not found" }

        val rental = Rental(clubId = clubId, courtId = courtId, userId = userId, startTime = startTime, duration = duration)
        CourtsDataMem.rentals[rental.rid] = rental
        return rental
    }

    fun getRentals(): List<Rental> = CourtsDataMem.rentals.values.toList()

    fun getRental(rid: String): Rental? = CourtsDataMem.rentals[rid]  // ✅ Eklendi
}
