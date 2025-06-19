package interfaces

import models.Club

interface IclubServices {
    fun createClub(name: String, userID: Int): Club
    fun getClubDetails(clubId: Int): Club?
    fun getAllClubs(): List<Club>
    fun searchClubsByName(partialName: String): List<Club>

}
