package storage

import models.*
import java.util.*

object CourtsDataMem {

    val courts = mutableMapOf<String, Court>()

    //  Kort ekleme fonksiyonu
    fun addCourt(name: String, clubId: String): Court {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }
        val court = Court(courtID = UUID.randomUUID().toString(), name = name, clubId = clubId)
        courts[court.courtID] = court
        return court
    }

    //  Kortun detaylarını almak (ID ile)
    fun getCourtById(crid: String): Court? = courts[crid]

    //  Tüm kortları listeleme
    fun getAllCourts(): List<Court> = courts.values.toList()

    //  Kortu isme göre bulma
    fun getCourtByName(name: String): Court? = courts.values.find { it.name == name }

    //  Belirli bir kulübe ait tüm kortları listeleme
    fun getCourtsForClub(clubId: String): List<Court> {
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }
        return courts.values.filter { it.clubId == clubId }
    }
}
