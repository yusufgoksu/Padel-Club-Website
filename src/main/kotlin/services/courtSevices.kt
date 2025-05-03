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
    fun getAllCourts(): List<Court> = CourtsDataMem.getAllCourts()

    // Kortu ID'ye göre getirme
    fun getCourtById(courtID: String): Court? {
        // Kortun var olup olmadığını kontrol et
        return CourtsDataMem.getCourtById(courtID)
    }

    // Kulübe ait kortları listeleme
    fun getCourtsForClub(clubId: String): List<Court> {

        // Kulübe ait kortları döndür
        return CourtsDataMem.getCourtsForClub(clubId)
    }

    fun getCourtByName(name: String): Court? {
        return CourtsDataMem.getCourtByName(name)
    }

}
