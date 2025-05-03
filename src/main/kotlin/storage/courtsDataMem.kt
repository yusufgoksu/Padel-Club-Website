package storage

import models.*
import java.util.*

object CourtsDataMem {

    val courts = mutableMapOf<String, Court>()

    //  Kort ekleme fonksiyonu
    fun addCourt(name: String, clubId: String): Court {
        // Kulübün varlığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }

        // Kortu oluştur
        val court = Court(courtID = UUID.randomUUID().toString(), name = name, clubId = clubId)

        // Kortu kaydet
        courts[court.courtID] = court
        return court
    }

    //  Kortun detaylarını almak (ID ile)
    fun getCourtById(crid: String): Court? {
        // Kortun varlığını kontrol et
        require(courts.containsKey(crid)) { "Court ID '$crid' not found" }
        return courts[crid]
    }

    //  Tüm kortları listeleme
    fun getAllCourts(): List<Court> = courts.values.toList()

    //  Kortu isme göre bulma
    fun getCourtByName(name: String): Court? {
        // İsme göre kort arama
        return courts.values.find { it.name == name }
    }

    //  Belirli bir kulübe ait tüm kortları listeleme
    fun getCourtsForClub(clubId: String): List<Court> {
        // Kulübün varlığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }

        // Kulübe ait kortları döndür
        return courts.values.filter { it.clubId == clubId }
    }
}
