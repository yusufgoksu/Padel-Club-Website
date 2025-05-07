package interfaces
import models.Club

interface IclubServices {
    fun createClub(name: String, ownerUid: Int): String

    fun getClubDetails(clubId: Int): Club?

    fun getAllClubs(): List<Club>
}