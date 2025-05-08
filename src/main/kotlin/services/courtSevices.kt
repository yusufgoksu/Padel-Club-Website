package services

import models.Court
import storage.ClubsDataMem
import storage.CourtsDataMem

object CourtServices {

    /**
     * Yeni bir kort ekler.
     * @throws IllegalArgumentException Geçersiz girişler için
     */
    fun addCourt(name: String, clubId: Int): Court {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataMem.getClubById(clubId) != null) { "Club ID '$clubId' not found" }

        return CourtsDataMem.addCourt(name = name, clubId = clubId)
    }

    /**
     * Tüm kortları döner.
     */
    fun getAllCourts(): List<Court> =
        CourtsDataMem.getAllCourts()

    /**
     * ID ile kort getirir.
     */
    fun getCourtById(courtID: Int): Court? {
        require(courtID > 0) { "Court ID must be greater than 0" }
        return CourtsDataMem.getCourtById(courtID)
    }

    /**
     * Belirli bir kulübe ait kortları listeler.
     */
    fun getCourtsForClub(clubId: Int): List<Court> {
        require(clubId > 0) { "Club ID must be greater than 0" }
        return CourtsDataMem.getCourtsForClub(clubId)
    }

    /**
     * İsme göre ilk eşleşen kortu getirir.
     */
    fun getCourtByName(name: String): Court? {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        return CourtsDataMem.getCourtByName(name)
    }
}
