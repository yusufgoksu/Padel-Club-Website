package storage

import models.Club
import java.util.concurrent.atomic.AtomicInteger

object ClubsDataMem {
    // clubID -> Club
    val clubs = mutableMapOf<Int, Club>()
    val idCounter = AtomicInteger(1)  // Sıralı clubID oluşturmak için

    /**
     * Yeni bir kulüp ekler ve oluşturulan Club nesnesini döner.
     * @throws IllegalArgumentException geçersiz girdiler için
     */
    fun addClub(name: String, userID: Int): Club {
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }
        require(UsersDataMem.users.containsKey(userID)) { "User ID '$userID' not found" }

        val clubID = idCounter.getAndIncrement()
        val club = Club(clubID = clubID, name = name, userID = userID)
        clubs[clubID] = club
        return club
    }

    /**
     * ID ile kulüp döner; bulunamazsa hata fırlatır.
     */
    fun getClubById(clubID: Int): Club? {
        require(clubID > 0) { "Club ID must be greater than 0" }
        require(clubs.containsKey(clubID)) { "Club ID '$clubID' not found" }
        return clubs[clubID]
    }

    /**
     * Tüm kulüpleri liste olarak döner.
     */
    fun getAllClubs(): List<Club> =
        clubs.values.toList()

    /**
     * Detaylı kulüp bilgisi.
     */
    fun getClubDetails(clubID: Int): Club? =
        getClubById(clubID)
}