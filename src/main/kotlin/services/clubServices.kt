package services

import models.Club
import data.database.ClubsDataDb
import data.database.UserDataDb

object ClubServices {

    fun addClub(name: String, userID: Int): Club {
        require(name.isNotBlank()) { "Club name cannot be empty" }
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }
        require(userID > 0) { "User ID must be greater than 0" }
        require(UserDataDb.getUserDetails(userID) != null) { "User ID '$userID' not found" }

        // Veritabanı otomatik clubID oluşturacak ve Club nesnesi dönecek
        return ClubsDataDb.createClub(name, userID)
    }



    // Tüm kulüpleri listeleme
    fun getAllClubs(): List<Club> =
        ClubsDataDb.getAllClubs()

    // ID'ye göre kulüp getirme
    fun getClubById(clubID: Int): Club? {
        require(clubID > 0) { "Club ID must be greater than 0" }
        return ClubsDataDb.getClubDetails(clubID)
    }

    // Kulüp detaylarını alma
    fun getClubDetails(clubID: Int): Club? =
        getClubById(clubID)


    fun searchClubsByName(partialName: String): List<Club> {
        println("🔍 Running SQL with: %$partialName%")
        require(partialName.isNotBlank()) { "Partial name cannot be empty" }
        return ClubsDataDb.searchClubsByName(partialName)
    }


}
