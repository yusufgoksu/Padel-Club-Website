package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem

object CourtServices {


    fun addCourt(name: String, clubId: String): Court {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }
        val court = Court(name = name, clubId = clubId)
        CourtsDataMem.courts[court.courtID] = court
        return court
    }

    fun getCourts(): List<Court> = CourtsDataMem.courts.values.toList()

    fun getCourtById(courtID: String): Court? = CourtsDataMem.courts[courtID]

}