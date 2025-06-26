package tests

import Database
import api.rentalsWebApi
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.Rental
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.*

class RentalTests {

    @BeforeEach
    fun setup() {
        Database.getConnection().use { conn ->
            conn.prepareStatement("DELETE FROM rentals;").executeUpdate()
            conn.prepareStatement("DELETE FROM courts;").executeUpdate()
            conn.prepareStatement("DELETE FROM clubs;").executeUpdate()
            conn.prepareStatement("DELETE FROM users;").executeUpdate()
        }
    }

    /* ---------- 1. GET /api/rentals/{id} ---------- */

    @Test
    fun `get rental by id returns correct rentalId`() {
        val user  = UserServices.createUser("Test", "test@example.com")
        val club  = ClubServices.addClub("Test Club", user.userId!!)
        val court = CourtServices.addCourt("Test Court", club.clubID!!)

        val created = RentalServices.addRental(
            clubId    = club.clubID!!,
            courtId   = court.courtID!!,
            userId    = user.userId!!,
            startTime = "2025-01-01T10:00:00",
            duration  = 1
        )

        val resp = rentalsWebApi()(Request(Method.GET, "/api/rentals/${created.rentalId}"))
        assertEquals(Status.OK, resp.status)

        val rentalFromApi: Rental = jacksonObjectMapper().readValue(resp.bodyString())
        assertEquals(created.rentalId, rentalFromApi.rentalId)
    }

    /* ---------- 2. OLUŞTURMA (GEÇERLİ) ---------- */

    @Test
    fun `create rental with valid data`() {
        val user  = UserServices.createUser("Renter", "renter@example.com")
        val club  = ClubServices.addClub("Tennis Club", user.userId!!)
        val court = CourtServices.addCourt("Court 1", club.clubID!!)
        val start = "2025-03-27T14:00:00"

        val rental = RentalServices.addRental(
            clubId = club.clubID!!,
            courtId = court.courtID!!,
            userId = user.userId!!,
            startTime = start,
            duration = 1
        )

        assertEquals(club.clubID,   rental.clubId)
        assertEquals(court.courtID, rental.courtId)
        assertEquals(user.userId,   rental.userId)
        assertEquals(start,         rental.startTime)
        assertEquals(1,             rental.duration)
    }

    /* ---------- 3. ÇAKIŞAN KİRALAMA ENGELİ ---------- */

    @Test
    fun `cannot create overlapping rental on same court`() {
        val user  = UserServices.createUser("OverlapUser", "ov@example.com")
        val club  = ClubServices.addClub("Overlap Club", user.userId!!)
        val court = CourtServices.addCourt("Center", club.clubID!!)

        // İlk kiralama 10-12
        RentalServices.addRental(
            clubId = club.clubID!!,
            courtId = court.courtID!!,
            userId = user.userId!!,
            startTime = "2025-05-01T10:00:00",
            duration = 2
        )

        // 11-12 arasında çakışır
        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID!!,
                courtId = court.courtID!!,
                userId = user.userId!!,
                startTime = "2025-05-01T11:00:00",
                duration = 1
            )
        }
        assertTrue(ex.message!!.contains("already"), "Çakışma mesajı bekleniyor")
    }

    /* ---------- 4. GEÇERSİZ KIMLIKLER ---------- */

    @Test
    fun `cannot create rental with invalid club`() {
        val user = UserServices.createUser("X", "x@e.com")
        val club = ClubServices.addClub("Valid", user.userId!!)
        val ct   = CourtServices.addCourt("Ct", club.clubID!!)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(9999, ct.courtID!!, user.userId!!, "2025-01-01T10:00:00", 1)
        }
        assertEquals("Club ID '9999' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user = UserServices.createUser("Y", "y@e.com")
        val club = ClubServices.addClub("Club", user.userId!!)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(club.clubID!!, 9999, user.userId!!, "2025-01-01T10:00:00", 1)
        }
        assertEquals("Court ID '9999' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val owner = UserServices.createUser("Owner", "o@e.com")
        val club  = ClubServices.addClub("Club", owner.userId!!)
        val court = CourtServices.addCourt("Court", club.clubID!!)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(club.clubID!!, court.courtID!!, 9999, "2025-01-01T10:00:00", 1)
        }
        assertEquals("User ID '9999' not found", ex.message)
    }

    /* ---------- 5. ID İLE GETİRME ---------- */

    @Test
    fun `retrieve entities by ID`() {
        // 1) Veri set-up
        val user  = UserServices.createUser("A", "a@e.com")
        val club  = ClubServices.addClub("C", user.userId!!)
        val court = CourtServices.addCourt("CO", club.clubID!!)
        val rent  = RentalServices.addRental(
            clubId    = club.clubID!!,
            courtId   = court.courtID!!,
            userId    = user.userId!!,
            startTime = "2025-01-01T10:00:00",
            duration  = 1
        )

        // 2) Assert – Her servis doğru nesneyi dönüyor mu?
        assertEquals(user.userId!!,
            UserServices.getUserById(user.userId!!)!!.userId)

        assertEquals(club.clubID!!,
            ClubServices.getClubById(club.clubID!!)!!.clubID)

        assertEquals(court.courtID!!,
            CourtServices.getCourtById(court.courtID!!)!!.courtID)

        assertEquals(rent.rentalId!!,
            RentalServices.getRentalById(rent.rentalId!!)!!.rentalId)
    }

    /* ---------- 6. COURT BAZLI LİSTE ---------- */

    @Test
    fun `get rentals for specific club and court`() {
        val u  = UserServices.createUser("U", "u@e.com")
        val cl = ClubServices.addClub("C", u.userId!!)
        val ct = CourtServices.addCourt("CO", cl.clubID!!)

        val r1 = RentalServices.addRental(cl.clubID!!, ct.courtID!!, u.userId!!, "2025-01-01T10:00:00", 1)
        val r2 = RentalServices.addRental(cl.clubID!!, ct.courtID!!, u.userId!!, "2025-01-01T12:00:00", 1)

        val list = RentalServices.getRentalsForCourt(cl.clubID!!, ct.courtID!!)
        assertEquals(setOf(r1.rentalId, r2.rentalId), list.map { it.rentalId }.toSet())
    }

    /* ---------- 7. SİLME & GÜNCELLEME ---------- */

    @Test
    fun `delete a rental`() {
        val u  = UserServices.createUser("U", "u@e.com")
        val cl = ClubServices.addClub("C", u.userId!!)
        val ct = CourtServices.addCourt("CO", cl.clubID!!)
        val r  = RentalServices.addRental(cl.clubID!!, ct.courtID!!, u.userId!!,"2025-04-11T10:00:00",1)

        assertTrue(RentalServices.deleteRental(r.rentalId!!))
        assertNull(RentalServices.getRentalById(r.rentalId!!))
    }

    @Test
    fun `update a rental`() {
        val u  = UserServices.createUser("U", "u@e.com")
        val cl = ClubServices.addClub("C", u.userId!!)
        val ct = CourtServices.addCourt("CO", cl.clubID!!)
        val r  = RentalServices.addRental(cl.clubID!!, ct.courtID!!, u.userId!!,"2025-04-11T10:00:00",1)

        val updated = RentalServices.updateRental(r.rentalId!!, "2025-04-11T11:00:00", 2)
        assertEquals("2025-04-11T11:00:00", updated.startTime)
        assertEquals(2, updated.duration)
    }
    @Test
    fun `getAvailableHours returns correct available hours`() {
        val user  = UserServices.createUser("U", "u@e.com")
        val club  = ClubServices.addClub("Club", user.userId!!)
        val court = CourtServices.addCourt("Court", club.clubID!!)

        // İki dolu saatlik kiralama: 10:00-11:00 ve 12:00-13:00
        RentalServices.addRental(club.clubID!!, court.courtID!!, user.userId!!, "2025-04-11T10:00:00", 1)
        RentalServices.addRental(club.clubID!!, court.courtID!!, user.userId!!, "2025-04-11T12:00:00", 1)

        val available = RentalServices.getAvailableHours(
            clubId = club.clubID!!,
            courtId = court.courtID!!,
            date = "2025-04-11"
        )

        // Beklenen uygun saatler: 08, 09, 11, 13, 14, 15, 16 (08:00–17:00 arası, 10 ve 12 dolu)
        val expected = listOf(8, 9, 11, 13, 14, 15, 16,17)

        assertEquals(expected, available)
    }


}
