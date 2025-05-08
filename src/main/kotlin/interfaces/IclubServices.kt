package interfaces
import models.Club

interface IclubServices {
    fun createClub(name: String, userID: Int): Int

    fun getClubDetails(clubId: Int): Club?

    fun getAllClubs(): List<Club>
}