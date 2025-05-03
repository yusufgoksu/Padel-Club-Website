package services

import models.*
import storage.ClubsDataMem
import storage.UsersDataMem

object ClubServices {

    fun addClub(name: String, ownerUid: String): Club {
        // Sahip UID'sinin geçerli olup olmadığını kontrol et
        require(UsersDataMem.users.containsKey(ownerUid)) { "Owner UID not found" }

        // Kulüp adı boş olamaz
        require(name.isNotBlank()) { "Club name cannot be empty" }

        // Kulüp adı 100 karakteri geçemez
        require(name.length <= 100) { "Club name cannot exceed 100 characters" }

        // Kulüp nesnesi oluşturuluyor
        val club = Club(name = name, ownerUid = ownerUid)

        // Kulübü veri hafızasına ekleyin
        ClubsDataMem.clubs[club.clubID] = club

        return club
    }


    fun getClubs(): List<Club> = ClubsDataMem.clubs.values.toList()

    fun getClubById(clubID: String): Club? = ClubsDataMem.clubs[clubID]



}
