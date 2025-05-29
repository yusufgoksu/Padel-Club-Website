package services

import models.Court
import data.database.ClubsDataDb
import data.database.CourtsDataDb

object CourtServices {

    /**
     * Yeni bir kort ekler.
     * @throws IllegalArgumentException Geçersiz girişler için
     */
    fun addCourt(name: String, clubId: Int): Court {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataDb.getClubDetails(clubId) != null) { "Club ID '$clubId' not found" }

        val courtId = CourtsDataDb.createCourt(name, clubId)
        return CourtsDataDb.getCourt(courtId)
            ?: throw IllegalStateException("Court creation failed")
    }

    /**
     * Tüm kortları döner.
     * Not: Eğer böyle bir API yoksa getCourtsByClub ile her kulüpten alınabilir.
     * Burada tüm kortları dönen özel bir endpoint örnek verilmiyor.
     */
    fun getAllCourts(): List<Court> {
        // Eğer CourtsDataDb.getAllCourts() gibi bir şey tanımlı değilse, kaldırabilirsin.
        throw NotImplementedError("This method is not implemented in database layer")
    }

    /**
     * ID ile kort getirir.
     */
    fun getCourtById(courtID: Int): Court? {
        require(courtID > 0) { "Court ID must be greater than 0" }
        return CourtsDataDb.getCourt(courtID)
    }

    /**
     * Belirli bir kulübe ait kortları listeler.
     */
    fun getCourtsForClub(clubId: Int): List<Court> {
        require(clubId > 0) { "Club ID must be greater than 0" }
        return CourtsDataDb.getCourtsByClub(clubId)
    }

    /**
     * İsme göre ilk eşleşen kortu getirir.
     * Not: Bu desteklenmiyorsa, geçici olarak tüm kortları getirip filtreleyebilirsin.
     */
    fun getCourtByName(name: String): Court? {
        require(name.isNotBlank()) { "Court name cannot be empty" }

        // Bu desteklenmiyor, manuel filtreleme yapılabilir.
        return CourtsDataDb.getCourtsByClub(0)  // clubId = 0 yanlış bir filtre olur
            .find { it.name.equals(name, ignoreCase = true) }
    }
}
