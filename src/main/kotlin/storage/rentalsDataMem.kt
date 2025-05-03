package storage
import java.time.LocalTime
import models.*
import storage.CourtsDataMem.courts
import storage.UsersDataMem.users
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RentalsDataMem {


    val rentals = mutableMapOf<String, Rental>()

    // Kiralama fonksiyonu
    fun addRental(clubID: String, userId: String, courtId: String, startTime: String, duration: Int): Rental {
        // Kullanıcı ve kortun varlığını kontrol et
        require(users.containsKey(userId)) { "User ID not found" }
        require(courts.containsKey(courtId)) { "Court ID not found" }
        // Kiralama süresi 0'dan küçük olamaz
        require(duration > 0) { "Rental duration must be greater than zero" }

        // StartTime'ın geçerli bir formatta olup olmadığını kontrol et
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsedStartTime = try {
            LocalDateTime.parse(startTime, formatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid start time format")
        }

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

    // Kulüp ve kort için kiralamaları almak
    fun getRentalsForClubAndCourt(cid: String, crid: String, date: String? = null): List<Rental> {
        val rentalsForClubAndCourt = rentals.values.filter {
            it.clubId == cid && it.courtId == crid
        }

        return if (date != null) {
            rentalsForClubAndCourt.filter { it.startTime.startsWith(date) }
        } else {
            rentalsForClubAndCourt
        }
    }

    // Kullanıcıya ait kiralamaları almak
    fun getRentalsForUser(userId: String): List<Rental> {
        return rentals.values.filter { it.userId == userId }
    }

    fun getAvailableHours(cid: String, crid: String, date: String): List<Int> {
        val rentedHours = rentals.values
            .filter { it.clubId == cid && it.courtId == crid && it.startTime.startsWith(date) }
            .mapNotNull {
                runCatching { LocalTime.parse(it.startTime.substring(11, 16)).hour }.getOrNull()
            }  // Saatleri güvenli bir şekilde al

        val allHours = (9..18).toList() // 9:00'dan 18:00'e kadar saatler
        return allHours - rentedHours   // Rented olmayan saatleri filtrele
    }

}

