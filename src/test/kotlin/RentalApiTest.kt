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
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }

    @Test
    fun `create rental with valid data`() {
        // Setup: create user, club, and court
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        val court = CourtServices.addCourt("Court 1", club.clubID)
        val validStartTime = "2024-07-01T10:00:00Z"  // ISO-8601 formatında

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = validStartTime,
            duration = 60
        )

        assertNotNull(rental.rentalID)
        assertEquals(club.clubID, rental.clubId)
        assertEquals(court.courtID, rental.courtId)
        assertEquals(user.userID, rental.userId)
        assertEquals("2024-07-01T10:00:00", rental.startTime)
        assertEquals(60, rental.duration)
    }

    @Test
    fun `cannot create rental with invalid club`() {
        val user = UserServices.addUser("Renter", "renter@example.com")

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = "non-existent-club",
                courtId = "court-id",
                userId = user.userID,
                startTime = "2024-07-01T10:00:00",
                duration = 60
            )
        }

        assertEquals("Club ID not found", exception.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = "non-existent-court",
                userId = user.userID,
                startTime = "2024-07-01T10:00:00",
                duration = 60
            )
        }

        assertEquals("Court ID not found", exception.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val user = UserServices.addUser("Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        val court = CourtServices.addCourt("Court 1", club.clubID)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = court.courtID,
                userId = "non-existent-user",
                startTime = "2024-07-01T10:00:00",
                duration = 60
            )
        }

        assertEquals("User ID not found", exception.message)
    }

    @Test
    fun `retrieve entities by ID`() {
        // Create test data
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )

        // Test retrievals
        assertEquals(user, UserServices.getUserById(user.userID))
        assertEquals(club, ClubServices.getClubById(club.clubID))
        assertEquals(court, CourtServices.getCourtById(court.courtID))
        assertEquals(rental, RentalServices.getRentalById(rental.rentalID))
    }

    @Test
    fun `get rentals for specific club and court`() {
        // Create test data
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T12:00:00",
            duration = 60
        )

        // Test rentals retrieval by club and court
        val rentals = RentalsDataMem.getRentalsForClubAndCourt(club.clubID, court.courtID, null)
        assertEquals(2, rentals.size)
        assertEquals(rental1.rentalID, rentals[0].rentalID)
        assertEquals(rental2.rentalID, rentals[1].rentalID)
    }

    @Test
    fun `get rentals for a specific user`() {
        // Create test data
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T12:00:00",
            duration = 60
        )

        // Test rentals retrieval by user
        val rentals = RentalsDataMem.getRentalsForUser(user.userID)
        assertEquals(2, rentals.size)
        assertEquals(rental1.rentalID, rentals[0].rentalID)
        assertEquals(rental2.rentalID, rentals[1].rentalID)
    }

    @Test
    fun `get available hours for court`() {
        // Create test data
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )

        // Test available hours after one rental
        val availableHours = RentalsDataMem.getAvailableHours(club.clubID, court.courtID, "2024-07-01")
        assertEquals(17, availableHours.size) // Assuming only one rental exists, it should remove the 10:00 hour
        assertFalse(availableHours.contains(10)) // 10:00 should be unavailable
    }
}