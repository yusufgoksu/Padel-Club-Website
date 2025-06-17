package interfaces

import models.Club

interface IclubServices {
    fun createClub(clubId: Int, name: String, userID: Int): Int
    fun getClubDetails(clubId: Int): Club?
    fun getAllClubs(): List<Club>
    fun searchClubsByName(partialName: String): List<Club>

    // ✅ Yeni fonksiyon: email ile club ekle
    fun addClub(userEmail: String, clubName: String): Club
}
