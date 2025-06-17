package tests

import api.rentalsWebApi
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.CourtServices
import services.RentalServices
import services.UserServices

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

    @Test
    fun `create rental with valid data`() {
        val userId = 1001
        val clubId = 2001
        val courtId = 3001
        val rentalId = 4001

        UserServices.CreateUser(userId, "Renter", "renter@example.com")
        ClubServices.addClub(clubId, "Tennis Club", userId)
        CourtServices.addCourt(courtId, "Court 1", clubId)

        val start = "2025-03-27T14:00:00"

        val rental = RentalServices.addRental(
            rentalId,
            clubId,
            courtId,
            userId,
            start,
            1
        )

        assertEquals(rentalId, rental.rentalID)
        assertEquals(clubId, rental.clubId)
        assertEquals(courtId, rental.courtId)
        assertEquals(userId, rental.userId)
        assertEquals(start, rental.startTime)
        assertEquals(1, rental.duration)
    }

    @Test
    fun `cannot create rental with invalid club`() {
        val userId = 1002
        val badClubId = 9999
        val courtId = 3002
        val rentalId = 4002

        UserServices.CreateUser(userId, "Renter", "renter2@example.com")
        CourtServices.addCourt(courtId, "Court 2", badClubId) // Invalid club

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                rentalId,
                badClubId,
                courtId,
                userId,
                "2024-07-01T10:00:00",
                1
            )
        }
        assertEquals("Club ID '$badClubId' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val userId = 1003
        val clubId = 2003
        val badCourtId = 9999
        val rentalId = 4003

        UserServices.CreateUser(userId, "Renter", "renter3@example.com")
        ClubServices.addClub(clubId, "Tennis Club 3", userId)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                rentalId,
                clubId,
                badCourtId,
                userId,
                "2024-07-01T10:00:00",
                1
            )
        }
        assertEquals("Court ID '$badCourtId' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val ownerId = 1004
        val clubId = 2004
        val courtId = 3004
        val badUserId = 9999
        val rentalId = 4004

        UserServices.CreateUser(ownerId, "Owner", "owner@example.com")
        ClubServices.addClub(clubId, "Tennis Club 4", ownerId)
        CourtServices.addCourt(courtId, "Court 4", clubId)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                rentalId,
                clubId,
                courtId,
                badUserId,
                "2024-07-01T10:00:00",
                1
            )
        }
        assertEquals("User ID '$badUserId' not found", ex.message)
    }

    @Test
    fun `retrieve entities by ID`() {
        val userId = 1010
        val clubId = 2010
        val courtId = 3010
        val rentalId = 4010

        UserServices.CreateUser(userId, "Test User", "test@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)
        RentalServices.addRental(
            rentalId,
            clubId,
            courtId,
            userId,
            "2024-07-01T10:00:00",
            1
        )

        val fetchedUser = UserServices.getUserById(userId)
        val fetchedClub = ClubServices.getClubById(clubId)
        val fetchedCourt = CourtServices.getCourtById(courtId)
        val fetchedRental = RentalServices.getRentalById(rentalId)

        assertEquals(userId, fetchedUser?.userId)
        assertEquals(clubId, fetchedClub?.clubID)
        assertEquals(courtId, fetchedCourt?.courtID)
        assertEquals(rentalId, fetchedRental?.rentalID)
    }

    @Test
    fun `get rentals for specific club and court`() {
        val userId = 1020
        val clubId = 2020
        val courtId = 3020
        val rentalId1 = 4020
        val rentalId2 = 4021

        UserServices.CreateUser(userId, "Test User", "test2@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        val r1 = RentalServices.addRental(rentalId1, clubId, courtId, userId, "2024-07-01T10:00:00", 1)
        val r2 = RentalServices.addRental(rentalId2, clubId, courtId, userId, "2024-07-01T12:00:00", 1)

        val list = RentalServices.getRentalsForClubAndCourt(clubId, courtId)
        assertEquals(2, list.size)
        assertEquals(r1.rentalID, list[0].rentalID)
        assertEquals(r2.rentalID, list[1].rentalID)
    }

    @Test
    fun `get rentals for a specific user`() {
        val userId = 1030
        val clubId = 2030
        val courtId = 3030
        val rentalId1 = 4030
        val rentalId2 = 4031

        UserServices.CreateUser(userId, "Test User", "test3@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        val r1 = RentalServices.addRental(rentalId1, clubId, courtId, userId, "2024-07-01T10:00:00", 1)
        val r2 = RentalServices.addRental(rentalId2, clubId, courtId, userId, "2024-07-01T12:00:00", 1)

        val list = RentalServices.getRentalsForUser(userId)
        assertEquals(2, list.size)
        assertEquals(r1.rentalID, list[0].rentalID)
        assertEquals(r2.rentalID, list[1].rentalID)
    }

    @Test
    fun `get available hours for court`() {
        val userId = 1040
        val clubId = 2040
        val courtId = 3040
        val rentalId = 4040

        UserServices.CreateUser(userId, "Test User", "test4@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        // Kiralama 10:00 - 13:00 arası 3 saat
        RentalServices.addRental(rentalId, clubId, courtId, userId, "2024-07-01T10:00:00", 3)

        val availableHours = RentalServices.getAvailableHours(clubId, courtId, "2024-07-01")
        println("Available hours: $availableHours")

        assertFalse(availableHours.contains(10))
        assertFalse(availableHours.contains(11))
        assertFalse(availableHours.contains(12))

        // Günün 24 saati üzerinden 3 saat dolu ise kalan 21 boş saat olur
        assertEquals(21, availableHours.size)
    }

    @Test
    fun `delete a rental`() {
        val userId = 1050
        val clubId = 2050
        val courtId = 3050
        val rentalId = 4050

        UserServices.CreateUser(userId, "Test User", "test5@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        val rental = RentalServices.addRental(rentalId, clubId, courtId, userId, "2025-04-11T10:00:00", 1)

        val deleted = RentalServices.deleteRental(rental.rentalID)
        val fetched = RentalServices.getRentalById(rental.rentalID)

        assertTrue(deleted)
        assertNull(fetched)
    }

    @Test
    fun `update a rental`() {
        val userId = 1060
        val clubId = 2060
        val courtId = 3060
        val rentalId = 4060

        UserServices.CreateUser(userId, "Test User", "test6@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        val rental = RentalServices.addRental(rentalId, clubId, courtId, userId, "2025-04-11T10:00:00", 1)

        val updated = RentalServices.updateRental(rental.rentalID, "2025-04-11T11:00:00", 2, courtId)

        assertEquals("2025-04-11T11:00:00", updated.startTime)
        assertEquals(2, updated.duration)
    }

    @Test
    fun `cannot create rental with negative duration`() {
        val userId = 1070
        val clubId = 2070
        val courtId = 3070
        val rentalId = 4070

        UserServices.CreateUser(userId, "Renter", "renter7@example.com")
        ClubServices.addClub(clubId, "Tennis Club", userId)
        CourtServices.addCourt(courtId, "Court 7", clubId)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(rentalId, clubId, courtId, userId, "2024-07-01T10:00:00", -1)
        }
        assertEquals("Duration must be between 1 and 10 hours", ex.message)
    }

    @Test
    fun `cannot create rental with invalid start time format`() {
        val userId = 1080
        val clubId = 2080
        val courtId = 3080
        val rentalId = 4080

        UserServices.CreateUser(userId, "Renter", "renter8@example.com")
        ClubServices.addClub(clubId, "Tennis Club", userId)
        CourtServices.addCourt(courtId, "Court 8", clubId)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(rentalId, clubId, courtId, userId, "invalid-datetime", 1)
        }
        assertEquals("Invalid startTime format; must be ISO-8601 ", ex.message)
    }

    @Test
    fun `get rentals for user with no rentals`() {
        val userId = 1090
        UserServices.CreateUser(userId, "Test User", "test9@example.com")
        val rentals = RentalServices.getRentalsForUser(userId)
        assertTrue(rentals.isEmpty())
    }

    @Test
    fun `get rentals for club and court with no rentals`() {
        val userId = 1100
        val clubId = 2100
        val courtId = 3100

        UserServices.CreateUser(userId, "Test User", "test10@example.com")
        ClubServices.addClub(clubId, "Test Club", userId)
        CourtServices.addCourt(courtId, "Test Court", clubId)

        val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtId)
        assertTrue(rentals.isEmpty())
    }

    @Test
    fun `get rental by invalid ID`() {
        val rental = RentalServices.getRentalById(9999)
        assertNull(rental)
    }

    @Test
    fun `should return users with rental counts for a specific court`() {
        val userId1 = 1111
        val userId2 = 1112
        val clubId = 2110
        val courtId = 3110

        UserServices.CreateUser(userId1, "User1", "u1@example.com")
        UserServices.CreateUser(userId2, "User2", "u2@example.com")
        ClubServices.addClub(clubId, "Club 1", userId1)
        CourtServices.addCourt(courtId, "Court 1", clubId)

        RentalServices.addRental(4111, clubId, courtId, userId1, "2024-07-01T10:00:00", 1)
        RentalServices.addRental(4112, clubId, courtId, userId1, "2024-07-01T11:00:00", 1)
        RentalServices.addRental(4113, clubId, courtId, userId2, "2024-07-01T12:00:00", 1)

        val request = Request(Method.GET, "api/courts/$courtId/users")
        val response = rentalsWebApi()(request)

        assertEquals(Status.OK, response.status)
        val body = response.bodyString()
        println("Response: $body")

        assertTrue(body.contains("\"userId\":$userId1"))
        assertTrue(body.contains("\"userId\":$userId2"))
    }
    @Test
    fun `should return courts with rental counts for a specific user`() {
        // Test verisini oluştur
        val user = UserServices.CreateUser(1, "User1", "user1@example.com")
        val club = ClubServices.addClub(1, "Club 1", user.userId)
        val court1 = CourtServices.addCourt(1, "Court 1", club.clubID)
        val court2 = CourtServices.addCourt(2, "Court 2", club.clubID)

        // Kiralamalar
        RentalServices.addRental(1, club.clubID, court1.courtID, user.userId, "2024-07-01T10:00:00", 1)
        RentalServices.addRental(2, club.clubID, court1.courtID, user.userId, "2024-07-01T11:00:00", 1)
        RentalServices.addRental(3, club.clubID, court2.courtID, user.userId, "2024-07-01T12:00:00", 1)

        // HTTP isteği simülasyonu
        val request = Request(Method.GET, "api/users/${user.userId}/courts")
        val response = rentalsWebApi()(request)

        assertEquals(Status.OK, response.status)
        val body = response.bodyString()

        assertTrue(body.contains("\"courtId\":${court1.courtID}"))
        assertTrue(body.contains("\"courtId\":${court2.courtID}"))
        assertTrue(body.contains("\"rentalCount\":2"))
        assertTrue(body.contains("\"rentalCount\":1"))
    }

}
