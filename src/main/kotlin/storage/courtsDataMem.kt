package storage

import models.*
import java.util.*

object CourtsDataMem {

    val courts = mutableMapOf<String, Court>()

    // Kort ekleme fonksiyonu
    fun addCourt(name: String, clubId: String): Court {
        // Club ID'nin geçerli olup olmadığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }

        // Yeni bir Court nesnesi oluştur
        val court = Court(courtID = UUID.randomUUID().toString(), name = name, clubId = clubId)

        // Kortu veritabanına ekle
        courts[court.courtID] = court
        return court
    }

    // Kort ID'sine göre kort almak
    fun getCourtById(crid: String): Court? = courts[crid]

    // Bütün kortları listele
    fun getAllCourts(): List<Court> = courts.values.toList()

    // Kort adını sorgulama
    fun getCourtByName(name: String): Court? {
        return courts.values.find { it.name == name }
    }
}
