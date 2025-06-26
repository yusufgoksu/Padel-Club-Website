package interfaces

import models.Court

interface IcourtServices {
        fun createCourt(name: String, clubId: Int): Court

        fun getCourt(courtId: Int): Court?

        fun getCourtsByClub(clubId: Int): List<Court>

        fun getAllCourts(): List<Court> // ✅ Yeni fonksiyon eklendi
}
