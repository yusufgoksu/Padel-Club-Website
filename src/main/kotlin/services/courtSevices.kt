package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem
import java.util.UUID

object CourtServices {


    fun addCourt(name: String, clubId: String): Court {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        val court = Court(name = name, clubId = clubId)
        CourtsDataMem.courts[court.crid] = court
        return court
    }

    fun getCourts(): List<Court> = CourtsDataMem.courts.values.toList()

    fun getCourtById(crid: String): Court? = CourtsDataMem.courts[crid]

}