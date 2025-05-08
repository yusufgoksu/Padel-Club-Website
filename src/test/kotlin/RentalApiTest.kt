package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.CourtServices
import services.RentalServices
import services.UserServices
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

class RentalTests {

    @BeforeEach
    fun setup() {
        // Clear in-memory stores
        UsersDataMem.users.clear()
        UsersDataMem.idCounter.set(1)
        ClubsDataMem.clubs.clear()
        ClubsDataMem.idCounter.set(1)
        CourtsDataMem.courts.clear()
        CourtsDataMem.idCounter.set(1)
        RentalsDataMem.getAllRentals().map { it.rentalID }
            .forEach { RentalsDataMem.deleteRental(it) }
    }

    @Test
    fun `create rental with valid data`() {
        val user  = UserServices.addUser("Renter", "renter@example.com")
        val club  = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)
        val start = "2025-03-27T14:00:00"

        val rental = RentalServices.addRental(
            clubId    = club.clubID,
            courtId   = court.courtID,
            userId    = user.userId,
            startTime = start,
            duration  = 1
        )

        assertNotNull(rental.rentalID)
        assertEquals(club.clubID, rental.clubId)
        assertEquals(court.courtID, rental.courtId)
        assertEquals(user.userId, rental.userId)
        assertEquals(start, rental.startTime)
        assertEquals(1, rental.duration)
    }

    @Test
    fun `cannot create rental with invalid club`() {
        val user      = UserServices.addUser("Renter", "renter@example.com")
        val badClubId = 999

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId    = badClubId,
                courtId   = 1,
                userId    = user.userId,
                startTime = "2024-07-01T10:00:00",
                duration  = 1
            )
        }
        assertEquals("Club ID '$badClubId' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user       = UserServices.addUser("Renter", "renter@example.com")
        val club       = ClubServices.addClub("Tennis Club", user.userId)
        val badCourtId = 999

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId    = club.clubID,
                courtId   = badCourtId,
                userId    = user.userId,
                startTime = "2024-07-01T10:00:00",
                duration  = 1
            )
        }
        assertEquals("Court ID '$badCourtId' not found", ex.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val owner      = UserServices.addUser("Owner", "owner@example.com")
        // first add a valid club & court so we hit the user check
        val club       = ClubServices.addClub("Tennis Club", owner.userId)
        val court      = CourtServices.addCourt("Court 1", club.clubID)
        val userIdBad  = 999

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId    = club.clubID,
                courtId   = court.courtID,
                userId    = userIdBad,
                startTime = "2024-07-01T10:00:00",
                duration  = 1
            )
        }
        assertEquals("User ID '$userIdBad' not found", ex.message)
    }

    @Test
    fun `retrieve entities by ID`() {
        val user   = UserServices.addUser("Test User", "test@example.com")
        val club   = ClubServices.addClub("Test Club", user.userId)
        val court  = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(
            clubId    = club.clubID,
            courtId   = court.courtID,
            userId    = user.userId,
            startTime = "2024-07-01T10:00:00",
            duration  = 1
        )

        assertEquals(user,   UserServices.getUserById(user.userId))
        assertEquals(club,   ClubServices.getClubById(club.clubID))
        assertEquals(court,  CourtServices.getCourtById(court.courtID))
        assertEquals(rental, RentalServices.getRentalById(rental.rentalID))
    }

    @Test
    fun `get rentals for specific club and court`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val r1 = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T10:00:00", 1)
        val r2 = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T12:00:00", 1)

        val list = RentalServices.getRentalsForClubAndCourt(club.clubID, court.courtID)
        assertEquals(2, list.size)
        assertEquals(r1.rentalID, list[0].rentalID)
        assertEquals(r2.rentalID, list[1].rentalID)
    }

    @Test
    fun `get rentals for a specific user`() {
        val user  = UserServices.addUser("Test User", "test@example.com")
        val club  = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val r1 = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T10:00:00", 1)
        val r2 = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T12:00:00", 1)

        val list = RentalServices.getRentalsForUser(user.userId)
        assertEquals(2, list.size)
        assertEquals(r1.rentalID, list[0].rentalID)
        assertEquals(r2.rentalID, list[1].rentalID)
    }

    @Test
    fun `get available hours for court`() {
        val user  = UserServices.addUser("Test User", "test@example.com")
        val club  = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T10:00:00", 3)

        val hours = RentalServices.getAvailableHours(club.clubID, court.courtID, "2024-07-01")
        // Should be 8..17 except 10,11,12 → total 7
        assertEquals(7, hours.size)
        assertFalse(hours.contains(10))
    }

    @Test
    fun `delete a rental`() {
        val user   = UserServices.addUser("Test User", "test@example.com")
        val club   = ClubServices.addClub("Test Club", user.userId)
        val court  = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2025-04-11T10:00:00", 1)

        val deleted = RentalServices.deleteRental(rental.rentalID)
        val fetched = RentalServices.getRentalById(rental.rentalID)

        assertTrue(deleted)
        assertNull(fetched)
    }

    @Test
    fun `update a rental`() {
        val user   = UserServices.addUser("Test User", "test@example.com")
        val club   = ClubServices.addClub("Test Club", user.userId)
        val court  = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(club.clubID, court.courtID, user.userId, "2025-04-11T10:00:00", 1)

        val updated = RentalServices.updateRental(rental.rentalID, "2025-04-11T11:00:00", 2, court.courtID)

        assertEquals("2025-04-11T11:00:00", updated.startTime)
        assertEquals(2, updated.duration)
    }

    @Test
    fun `cannot create rental with negative duration`() {
        val user  = UserServices.addUser("Renter", "renter@example.com")
        val club  = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(club.clubID, court.courtID, user.userId, "2024-07-01T10:00:00", -1)
        }
        assertEquals("Duration must be between 1 and 10 hours", ex.message)
    }

    @Test
    fun `cannot create rental with invalid start time format`() {
        val user  = UserServices.addUser("Renter", "renter@example.com")
        val club  = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)

        val ex = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(club.clubID, court.courtID, user.userId, "invalid-datetime", 1)
        }
        assertEquals(
            "Invalid startTime format; must be ISO-8601 ",
            ex.message
        )
    }

    @Test
    fun `get rentals for user with no rentals`() {
        val user    = UserServices.addUser("Test User", "test@example.com")
        val rentals = RentalServices.getRentalsForUser(user.userId)
        assertTrue(rentals.isEmpty())
    }

    @Test
    fun `get rentals for club and court with no rentals`() {
        val user  = UserServices.addUser("Test User", "test@example.com")
        val club  = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rentals = RentalServices.getRentalsForClubAndCourt(club.clubID, court.courtID)
        assertTrue(rentals.isEmpty())
    }

    @Test
    fun `get rental by invalid ID`() {
        val rental = RentalServices.getRentalById(999)
        assertNull(rental)
    }
}
