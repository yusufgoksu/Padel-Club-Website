package storage

import models.*
import java.util.*

object CourtsDataMem {

    val courts = mutableMapOf<String, Court>()


    fun addCourt(name: String, clubId: String): Court {
        val court = Court(crid = UUID.randomUUID().toString(), name = name, clubId = clubId)
        courts[court.crid] = court
        return court
    }

    // Optional: Add methods to retrieve entities
    fun getCourtById(crid: String): Court? = courts[crid]

    // Optional: Add methods to list all entities
    fun getAllCourts(): List<Court> = courts.values.toList()

}
