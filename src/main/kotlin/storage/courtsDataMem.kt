package storage

import models.Court
import java.util.concurrent.atomic.AtomicInteger

object CourtsDataMem {

    // courtID -> Court
    val courts = mutableMapOf<Int, Court>()
    val idCounter = AtomicInteger(1)  // Sıralı courtID oluşturmak için

    /** Yeni bir kort ekler ve oluşturulan Court nesnesini döner. */
    fun addCourt(name: String, clubId: Int): Court {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataMem.getClubById(clubId) != null) { "Club ID '$clubId' not found" }

        val courtID = idCounter.getAndIncrement()
        val court = Court(courtId = courtID, name = name, clubId = clubId)
        courts[courtID] = court
        return court
    }

    /** ID ile kortu getirir; bulunamazsa hata fırlatır. */
    fun getCourtById(courtID: Int?): Court? {
        if (courtID != null) {
            require(courtID > 0) { "Court ID must be greater than 0" }
        }
        require(courts.containsKey(courtID)) { "Court ID '$courtID' not found" }
        return courts[courtID]
    }

    /** Tüm kortları listeler. */
    fun getAllCourts(): List<Court> =
        courts.values.toList()

    /** İsme göre ilk eşleşen kortu bulur. */
    fun getCourtByName(name: String): Court? {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        return courts.values.find { it.name == name }
    }

    /** Belirli bir kulübe ait tüm kortları listeler. */
    fun getCourtsForClub(clubId: Int): List<Court> {
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataMem.getClubById(clubId) != null) { "Club ID '$clubId' not found" }
        return courts.values.filter { it.clubId == clubId }
    }
}
