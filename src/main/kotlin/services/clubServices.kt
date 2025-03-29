package services

import models.*
import storage.ClubsDataMem
import storage.UsersDataMem

object ClubServices {

    fun addClub(name: String, ownerUid: String): Club {
        require(UsersDataMem.users.containsKey(ownerUid)) { "Owner UID not found" }
        val club = Club(name = name, ownerUid = ownerUid)
        ClubsDataMem.clubs[club.clubID] = club
        return club
    }

    fun getClubs(): List<Club> = ClubsDataMem.clubs.values.toList()

    fun getClubById(clubID: String): Club? = ClubsDataMem.clubs[clubID]



}
