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
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }

    @Test
    fun `create rental with valid data`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        val court = CourtServices.addCourt("Court 1", club.clubID)
        val validStartTime = "2024-07-01T10:00:00Z"

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = validStartTime,
            duration = 1
        )

        assertNotNull(rental.rentalID)
        assertEquals(club.clubID, rental.clubId)
        assertEquals(court.courtID, rental.courtId)
        assertEquals(user.userID, rental.userId)
        assertEquals(validStartTime, rental.startTime)
        assertEquals(1, rental.duration)
    }

    @Test
    fun `cannot create rental with invalid club`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val clubID="non-existent-clubid"

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = clubID,
                courtId = "court-id",
                userId = user.userID,
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("Club ID $clubID' not found" , exception.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        val courtID="non-existent-courtid"

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = courtID,
                userId = user.userID,
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("Court ID $courtID' not found", exception.message)
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
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("User ID not found", exception.message)
    }

    @Test
    fun `retrieve entities by ID`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )

        assertEquals(user, UserServices.getUserById(user.userID))
        assertEquals(club, ClubServices.getClubById(club.clubID))
        assertEquals(court, CourtServices.getCourtById(court.courtID))
        assertEquals(rental, RentalServices.getRentalById(rental.rentalID))
    }

    @Test
    fun `get rentals for specific club and court`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T12:00:00Z",
            duration = 1
        )

        val rentals = RentalsDataMem.getRentalsForClubAndCourt(club.clubID, court.courtID, null)
        assertEquals(2, rentals.size)
        assertEquals(rental1.rentalID, rentals[0].rentalID)
        assertEquals(rental2.rentalID, rentals[1].rentalID)
    }

    @Test
    fun `get rentals for a specific user`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T12:00:00Z",
            duration = 1
        )

        val rentals = RentalsDataMem.getRentalsForUser(user.userID)
        assertEquals(2, rentals.size)
        assertEquals(rental1.rentalID, rentals[0].rentalID)
        assertEquals(rental2.rentalID, rentals[1].rentalID)
    }

    @Test
    fun `get available hours for court`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2024-07-01T10:00:00Z",
            duration = 3
        )

        val availableHours = RentalsDataMem.getAvailableHours(club.clubID, court.courtID, "2024-07-01")
        assertEquals(7, availableHours.size)
        assertFalse(availableHours.contains(10))
    }

    @Test
    fun `delete a rental`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2025-04-11T10:00:00Z",
            duration = 1
        )

        val deleted = RentalServices.deleteRental(rental.rentalID)
        val fetched = RentalServices.getRentalById(rental.rentalID)

        assertEquals(true, deleted)
        assertNull(fetched)
    }

    @Test
    fun `update a rental`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userID)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userID,
            startTime = "2025-04-11T10:00:00Z",
            duration = 1
        )

        val updatedRental = RentalServices.updateRental(
            rentalID = rental.rentalID,
            newStartTime = "2025-04-11T11:00:00Z",
            newDuration = 2,
            newCourtId = court.courtID
        )

        assertEquals("2025-04-11T11:00:00Z", updatedRental.startTime)
        assertEquals(2, updatedRental.duration)
    }
}
