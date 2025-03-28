package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.UsersDataMem
import java.util.UUID

object ClubServices {

    fun addClub(name: String, ownerUid: String): Club {
        require(UsersDataMem.users.containsKey(ownerUid)) { "Owner UID not found" }
        val club = Club(name = name, ownerUid = ownerUid)
        ClubsDataMem.clubs[club.cid] = club
        return club
    }

    fun getClubs(): List<Club> = ClubsDataMem.clubs.values.toList()

    fun getClubById(cid: String): Club? = ClubsDataMem.clubs[cid]



}
