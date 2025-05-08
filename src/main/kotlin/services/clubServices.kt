package services

import models.Club
import storage.ClubsDataMem
import storage.UsersDataMem

object ClubServices {

    // Yeni bir kulüp ekleme
    fun addClub(name: String, userID: Int): Club {
        // Input doğrulamaları
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }
        require(UsersDataMem.users.containsKey(userID)) { "User ID '$userID' not found" }

        // Depolama katmanına delege et
        return ClubsDataMem.addClub(name = name, userID = userID)
    }

    // Tüm kulüpleri listeleme
    fun getAllClubs(): List<Club> =
        ClubsDataMem.getAllClubs()

    // ID'ye göre kulüp getirme
    fun getClubById(clubID: Int): Club? {
        require(clubID > 0) { "Club ID must be greater than 0" }
        return ClubsDataMem.getClubById(clubID)
    }

    // Kulüp detaylarını alma
    fun getClubDetails(clubID: Int): Club? =
        getClubById(clubID)
}