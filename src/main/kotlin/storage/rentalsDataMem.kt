package storage
import storage.UsersDataMem
import storage.ClubsDataMem
import storage.CourtsDataMem
import models.*
import java.util.*

object RentalsDataMem {
    val users = mutableMapOf<String, User>()

    val courts = mutableMapOf<String, Court>()
    val rentals = mutableMapOf<String, Rental>()


    fun addRental(userId: String, courtId: String, startTime: String, duration: Int): Rental {
        require(courts.containsKey(courtId)) { "Court ID not found" }
        require(users.containsKey(userId)) { "User ID not found" }

        val rental = Rental(
            rid = UUID.randomUUID().toString(),
            clubId = courts[courtId]?.clubId ?: throw IllegalArgumentException("Court's club ID is missing"),
            courtId = courtId,
            userId = userId,
            startTime = startTime,
            duration = duration
        )
        rentals[rental.rid] = rental
        return rental
    }

    // Optional: Add methods to retrieve entities

    fun getRentalById(rid: String): Rental? = rentals[rid]

    // Optional: Add methods to list all entities

    fun getAllRentals(): List<Rental> = rentals.values.toList()
}
