package interfaces
import models.Court

interface IcourtServices {
        fun createCourt(courtId: Int, name: String, clubId: Int): Int

        fun getCourt(courtId: Int): Court?

        fun getCourtsByClub(clubId: Int): List<Court>
}