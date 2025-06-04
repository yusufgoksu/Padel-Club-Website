package interfaces
import models.Rental

interface IrentalService {
    fun createRental(rentalId: Int, clubId: Int, courtId: Int, userId: Int, date: String, duration: Int): Int

    fun getRentalDetails(rentalId: Int): Rental?

    fun getRentals(clubId: Int, courtId: Int, date: String? = null): List<Rental>

    fun getUserRentals(userId: Int): List<Rental>

    fun getAvailableRentalHours(clubId: Int, courtId: Int, date: String): List<String>

    fun deleteRental(rentalId: Int): Boolean

    fun updateRental(rentalId: Int, date: String, duration: Int): Boolean

}