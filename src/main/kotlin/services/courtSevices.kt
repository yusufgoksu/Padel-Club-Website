package services

import models.*
import storage.ClubsDataMem
import storage.CourtsDataMem

object CourtServices {


    fun addCourt(name: String, clubId: String): Court {
        // Kulüp ID'sinin geçerli olup olmadığını kontrol et
        require(ClubsDataMem.clubs.containsKey(clubId)) { "Club ID not found" }

        // Court isminin boş olamayacağını kontrol et
        require(name.isNotBlank()) { "Court name cannot be empty" }

        // Court isminin uzunluğunun 100 karakteri aşmaması gerektiğini kontrol et
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }

        // Yeni Court nesnesi oluştur
        val court = Court(name = name, clubId = clubId)

        // Court'u hafızaya ekle
        CourtsDataMem.courts[court.courtID] = court

        return court
    }


    fun getCourts(): List<Court> = CourtsDataMem.courts.values.toList()

    fun getCourtById(courtID: String): Court? = CourtsDataMem.courts[courtID]

}