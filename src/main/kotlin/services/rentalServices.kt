package services

import models.Rental
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

object RentalServices {

    /**
     * Yeni bir kiralama ekler.
     * @throws IllegalArgumentException Geçersiz girişler için
     */
    fun addRental(
        clubId: Int,
        courtId: Int,
        userId: Int,
        startTime: String,
        duration: Int
    ): Rental {
        require(clubId > 0) { "Club ID must be greater than 0" }
        require(ClubsDataMem.getClubById(clubId) != null) { "Club ID '$clubId' not found" }
        require(courtId > 0) { "Court ID must be greater than 0" }
        require(CourtsDataMem.getCourtById(courtId) != null) { "Court ID '$courtId' not found" }
        require(userId > 0) { "User ID must be greater than 0" }
        require(UsersDataMem.getUserById(userId) != null) { "User ID '$userId' not found" }

        return RentalsDataMem.addRental(clubId, courtId, userId, startTime, duration)
    }

    /** ID'ye göre kiralamayı getirir */
    fun getRentalById(rentalID: Int): Rental? =
        RentalsDataMem.getRentalById(rentalID)

    /** Tüm kiralamaları listeler */
    fun getAllRentals(): List<Rental> =
        RentalsDataMem.getAllRentals()

    /** Kulüp ve kort bazlı kiralamaları getirir */
    fun getRentalsForClubAndCourt(
        clubId: Int,
        courtId: Int,
        date: String? = null
    ): List<Rental> =
        RentalsDataMem.getRentalsForClubAndCourt(clubId, courtId, date)

    /** Kullanıcı bazlı kiralamaları getirir */
    fun getRentalsForUser(userId: Int): List<Rental> =
        RentalsDataMem.getRentalsForUser(userId)

    /** ID ile kiralamayı siler */
    fun deleteRental(rentalID: Int): Boolean =
        RentalsDataMem.deleteRental(rentalID)

    /** ID ile kiralamayı günceller */
    fun updateRental(
        rentalID: Int,
        newStartTime: String,
        newDuration: Int,
        newCourtId: Int
    ): Rental =
        RentalsDataMem.updateRental(rentalID, newStartTime, newDuration, newCourtId)

    /** Belirli bir tarih için uygun saatleri getirir */
    fun getAvailableHours(
        clubId: Int,
        courtId: Int,
        date: String
    ): List<Int> =
        RentalsDataMem.getAvailableHours(clubId, courtId, date)
}
