package storage

import models.*
import java.util.*

object ClubsDataMem {

    val clubs = mutableMapOf<String, Club>()

    // Kulüp ekleme fonksiyonu
    fun addClub(name: String, ownerId: String): Club {
        // Kullanıcının varlığını kontrol et
        require(UsersDataMem.users.containsKey(ownerId)) { "User ID '$ownerId' not found" }

        // Kulübü oluştur
        val club = Club(clubID = UUID.randomUUID().toString(), name = name, ownerUid = ownerId)

        // Kulübü kaydet
        clubs[club.clubID] = club
        return club
    }

    // Kulübün detaylarını almak (ID ile)
    fun getClubById(clubID: String): Club? {
        // Kulübün varlığını kontrol et
        require(clubs.containsKey(clubID)) { "Club ID '$clubID' not found" }
        return clubs[clubID]
    }

    // Tüm kulüpleri listeleme
    fun getAllClubs(): List<Club> = clubs.values.toList()

    // Kulüp bilgilerini almak (Detay)
    fun getClubDetails(cid: String): Club? {
        return getClubById(cid) // Bu fonksiyon zaten getClubById'yi çağırıyor
    }
}
