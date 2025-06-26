package services

import models.Court
import data.database.ClubsDataDb
import data.database.CourtsDataDb

object CourtServices {

    fun addCourt(name: String, clubId: Int): Court {
        require(name.isNotBlank()) { "Court name cannot be empty" }
        require(name.length <= 100) { "Court name cannot exceed 100 characters" }
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataDb.getClubDetails(clubId) != null) { "Club ID '$clubId' not found" }

        // Aynı kulüp altında aynı isimde kort var mı?
        val existingCourt = CourtsDataDb.getCourtsByClub(clubId)
            .any { it.name.equals(name, ignoreCase = true) }
        require(!existingCourt) { "A court with the same name already exists in this club." }

        return CourtsDataDb.createCourt(name, clubId)
    }


    fun getAllCourts(): List<Court> =
        CourtsDataDb.getAllCourts()


    fun getCourtById(courtID: Int): Court? {
        require(courtID > 0) { "Court ID must be greater than 0" }
        return CourtsDataDb.getCourt(courtID)
    }

    fun getCourtsForClub(clubId: Int): List<Court> {
        require(clubId > 0) { "Club ID must be greater than 0" }
        return CourtsDataDb.getCourtsByClub(clubId)
    }

    fun getCourtByName(name: String): Court? {
        require(name.isNotBlank()) { "Court name cannot be empty" }

        // Geçici çözüm: Tüm kulüpleri alıp her biri için kortları filtrele
        val allClubs = ClubsDataDb.getAllClubs() // Bu metod varsa
        return allClubs.asSequence()
            .flatMap { CourtsDataDb.getCourtsByClub(it.clubID!!) }
            .find { it.name.equals(name, ignoreCase = true) }
    }
}
