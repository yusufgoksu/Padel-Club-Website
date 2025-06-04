package services

import models.Rental
import data.database.ClubsDataDb
import data.database.CourtsDataDb
import data.database.UserDataDb
import data.database.RentalDataDb

object RentalServices {

    /**
     * Yeni bir kiralama ekler.
     * @throws IllegalArgumentException Geçersiz girişler için
     */
    fun addRental(
        rentalId: Int,
        clubId: Int,
        courtId: Int,
        userId: Int,
        startTime: String,
        duration: Int
    ): Rental {
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataDb.getClubDetails(clubId) != null) { "Club ID '$clubId' not found" }
        require(courtId > 0) { "Court ID must be greater than 0" }
        require(CourtsDataDb.getCourt(courtId) != null) { "Court ID '$courtId' not found" }
        require(userId > 0) { "User ID must be greater than 0" }
        require(UserDataDb.getUserDetails(userId) != null) { "User ID '$userId' not found" }

        RentalDataDb.createRental(rentalId, clubId, courtId, userId, startTime, duration)

        return RentalDataDb.getRentalDetails(rentalId)
            ?: throw IllegalStateException("Rental creation failed")
    }


    /** ID'ye göre kiralamayı getirir */
    fun getRentalById(rentalID: Int): Rental? =
        RentalDataDb.getRentalDetails(rentalID)

    /** Tüm kiralamaları listeler */
    fun getAllRentals(): List<Rental> {
        // Böyle bir fonksiyon doğrudan yoksa, tüm kullanıcı kiralamalarını birleştirebilirsin.
        // Örneğin:
        val allUsers = UserDataDb.getAllUsers()
        return allUsers.flatMap { user -> RentalDataDb.getUserRentals(user.userId) }
    }

    /** Kulüp ve kort bazlı kiralamaları getirir (tarihsiz) */
    fun getRentalsForClubAndCourt(
        clubId: Int,
        courtId: Int
    ): List<Rental> =
        RentalDataDb.getRentals(clubId, courtId, null)

    /** Kullanıcı bazlı kiralamaları getirir */
    fun getRentalsForUser(userId: Int): List<Rental> =
        RentalDataDb.getUserRentals(userId)

    /** ID ile kiralamayı siler */
    fun deleteRental(rentalID: Int): Boolean =
        RentalDataDb.deleteRental(rentalID)

    /** ID ile kiralamayı günceller */
    fun updateRental(
        rentalID: Int,
        newStartTime: String,
        newDuration: Int,
        newCourtId: Int
    ): Rental {
        // Not: Veritabanı update fonksiyonu sadece tarih ve süre güncelliyor
        val success = RentalDataDb.updateRental(rentalID, newStartTime, newDuration)
        if (!success) throw IllegalStateException("Rental update failed")
        return RentalDataDb.getRentalDetails(rentalID)
            ?: throw IllegalStateException("Rental not found after update")
    }

    /** Belirli bir tarih için uygun saatleri getirir */
    fun getAvailableHours(
        clubId: Int,
        courtId: Int,
        date: String
    ): List<Int> =
        RentalDataDb.getAvailableRentalHours(clubId, courtId, date).mapNotNull {
            it.split(" ").getOrNull(1)?.split(":")?.getOrNull(0)?.toIntOrNull()
        }


}
