package storage

import models.*
import java.util.*

object ClubsDataMem {

    val clubs = mutableMapOf<String, Club>()


    fun addClub(name: String, ownerId: String): Club {
        val club = Club(cid = UUID.randomUUID().toString(), name = name, ownerUid = ownerId)
        clubs[club.cid] = club
        return club
    }

    // Optional: Add methods to retrieve entities

    fun getClubById(cid: String): Club? = clubs[cid]

    // Optional: Add methods to list all entities
    fun getAllClubs(): List<Club> = clubs.values.toList()

}
