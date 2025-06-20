package storage

import models.Rental
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

object RentalsDataMem {

    /* rentalID -> Rental */
    private val rentals    = mutableMapOf<Int, Rental>()
    private val idCounter  = AtomicInteger(1)

    /**
     * Yeni rental ekler ve oluşturulan Rental nesnesini döndürür.
     * rentalID otomatik artar (in-memory sayaç).
     */
    fun addRental(
        clubId: Int,
        courtId: Int,      // ❗ artık nullable değil
        userId: Int,
        startTime: String,
        duration: Int
    ): Rental {
        require(clubId > 0)  { "Club ID must be greater than 0" }
        require(courtId > 0) { "Court ID must be greater than 0" }
        require(userId > 0)  { "User ID must be greater than 0" }

        require(ClubsDataMem.getClubById(clubId) != null)     { "Club ID '$clubId' not found" }
        require(CourtsDataMem.getCourtById(courtId) != null)  { "Court ID '$courtId' not found" }
        require(UsersDataMem.getUserById(userId) != null)     { "User ID '$userId' not found" }

        require(duration in 1..10) { "Duration must be between 1 and 10 hours" }

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val parsed = try {
            LocalDateTime.parse(startTime, formatter)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid startTime format; must be ISO-8601", e)
        }
        require(parsed.hour in 8..17) { "Start time hour must be between 08 and 17" }

        val rentalID = idCounter.getAndIncrement()
        val rental = Rental(
            rentalID  = rentalID,
            clubId    = clubId,
            courtId   = courtId,
            userId    = userId,
            startTime = startTime,
            duration  = duration
        )
        rentals[rentalID] = rental
        return rental
    }


    /* ID'ye göre rental getirir. */
    fun getRentalById(rentalID: Int): Rental? {
        require(rentalID > 0) { "Rental ID must be greater than 0" }
        return rentals[rentalID]
    }

    /* Tüm rental'ları listeler. */
    fun getAllRentals(): List<Rental> = rentals.values.toList()

    /* Kulüp + kort bazlı (isteğe bağlı tarih) filtre. */
    fun getRentalsForClubAndCourt(
        clubId: Int,
        courtId: Int,
        date: String? = null
    ): List<Rental> = getAllRentals().filter { r ->
        r.clubId == clubId && r.courtId == courtId &&
                (date == null || r.startTime.startsWith(date))
    }

    /* Kullanıcı bazlı rental'ları getirir. */
    fun getRentalsForUser(userId: Int): List<Rental> =
        getAllRentals().filter { it.userId == userId }

    /* Belirli tarih için 08-17 arası müsait saatler. */
    fun getAvailableHours(clubId: Int, courtId: Int, date: String): List<Int> {
        val occupied = getRentalsForClubAndCourt(clubId, courtId, date).flatMap { r ->
            val start = LocalDateTime.parse(r.startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).hour
            (start until start + r.duration)
        }
        return (8..17).filterNot { it in occupied }
    }

    /* Rental sil. */
    fun deleteRental(rentalID: Int): Boolean =
        rentals.remove(rentalID) != null

    /* Rental güncelle (sil-ekle yaklaşımı). */
    fun updateRental(
        rentalID: Int,
        newStartTime: String,
        newDuration: Int,
        newCourtId: Int
    ): Rental {
        val existing = getRentalById(rentalID)
            ?: throw IllegalArgumentException("Rental ID '$rentalID' not found")

        deleteRental(rentalID)

        return addRental(
            clubId = existing.clubId,
            courtId = newCourtId,
            userId = existing.userId,
            startTime = newStartTime,
            duration = newDuration
        )
    }
}
