package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem

object CourtServices {

    // Kort ekleme işlemi
    fun addCourt(name: String, clubId: String): Court {
        // Kulüp ID'sinin geçerli olup olmadığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }

        // Kort isminin boş olamayacağını kontrol et
        require(name.isNotBlank()) { "Court name cannot be empty" }

        // Kort isminin uzunluğunun 100 karakteri aşmaması gerektiğini kontrol et
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }

        // Yeni Kort nesnesi oluşturuluyor
        val court = Court(name = name, clubId = clubId)

        // Kortu hafızaya ekle
        CourtsDataMem.courts[court.courtID] = court

        return court
    }

    // Tüm kortları listeleme
    fun getCourts(): List<Court> = CourtsDataMem.courts.values.toList()

    // Kortu ID'ye göre getirme
    fun getCourtById(courtID: String): Court? {
        // Kortun var olup olmadığını kontrol et
        return CourtsDataMem.courts[courtID] ?: throw IllegalArgumentException("Court ID '$courtID' not found")
    }

    // Kulübe ait kortları listeleme
    fun getCourtsForClub(clubId: String): List<Court> {
        // Kulüp ID'sinin geçerli olup olmadığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID '$clubId' not found" }

        // Kulübe ait kortları döndür
        return CourtsDataMem.courts.values.filter { it.clubId == clubId }
    }

    // Kortu isme göre arama
    fun getCourtByName(name: String): Court? {
        return CourtsDataMem.courts.values.find { it.name.equals(name, ignoreCase = true) }
    }
}
