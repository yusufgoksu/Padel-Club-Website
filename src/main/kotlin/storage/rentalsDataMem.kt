package storage

import models.*
import java.util.*

object RentalsDataMem {

    val users = mutableMapOf<String, User>()
    val clubs = mutableMapOf<String, Club>()
    val courts = mutableMapOf<String, Court>()
    val rentals = mutableMapOf<String, Rental>()

    // Kiralama fonksiyonu
    fun addRental(clubID:String,userId: String, courtId: String, startTime: String, duration: Int): Rental {



        // Rental nesnesini oluştur
        val rental = Rental(
            rentalID = UUID.randomUUID().toString(),
            clubId = clubID,
            courtId = courtId,
            userId = userId,
            startTime = startTime,
            duration = duration
        )

        // Rental verisini kiralamalar listesine ekle
        rentals[rental.rentalID] = rental
        return rental
    }

    // Kiralama ID'sine göre kiralamayı almak
    fun getRentalById(rid: String): Rental? = rentals[rid]

    // Bütün kiralamaları listelemek
    fun getAllRentals(): List<Rental> = rentals.values.toList()
}
