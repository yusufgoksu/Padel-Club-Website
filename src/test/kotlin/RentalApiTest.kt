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
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)
        val validStartTime = "2024-07-01T10:00:00Z"

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
            startTime = validStartTime,
            duration = 1
        )

        assertNotNull(rental.rentalID)
        assertEquals(club.clubID, rental.clubId)
        assertEquals(court.courtID, rental.courtId)
        assertEquals(user.userId, rental.userId)
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
                userId = user.userId,
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("Club ID $clubID' not found" , exception.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val courtID="non-existent-courtid"

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = courtID,
                userId = user.userId,
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("Court ID $courtID' not found", exception.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val user = UserServices.addUser("Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)
        val userID="non-existent-userid"



        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = court.courtID,
                userId = userID,
                startTime = "2024-07-01T10:00:00Z",
                duration = 1
            )
        }

        assertEquals("User ID $userID' not found", exception.message)
    }

    @Test
    fun `retrieve entities by ID`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )

        assertEquals(user, UserServices.getUserById(user.userId))
        assertEquals(club, ClubServices.getClubById(club.clubID))
        assertEquals(court, CourtServices.getCourtById(court.courtID))
        assertEquals(rental, RentalServices.getRentalById(rental.rentalID))
    }

    @Test
    fun `get rentals for specific club and court`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
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
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )
        val rental2 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
            startTime = "2024-07-01T12:00:00Z",
            duration = 1
        )

        val rentals = RentalsDataMem.getRentalsForUser(user.userId)
        assertEquals(2, rentals.size)
        assertEquals(rental1.rentalID, rentals[0].rentalID)
        assertEquals(rental2.rentalID, rentals[1].rentalID)
    }

    @Test
    fun `get available hours for court`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)
        val rental1 = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
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
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
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
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rental = RentalServices.addRental(
            clubId = club.clubID,
            courtId = court.courtID,
            userId = user.userId,
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
    @Test
    fun `cannot create rental with negative duration`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = court.courtID,
                userId = user.userId,
                startTime = "2024-07-01T10:00:00Z",
                duration = -1 // Geçersiz süre
            )
        }

        assertEquals("Duration must be between 1-10 hours", exception.message)
    }

    @Test
    fun `cannot create rental with invalid start time format`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Court 1", club.clubID)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.clubID,
                courtId = court.courtID,
                userId = user.userId,
                startTime = "invalid-date-time", // Geçersiz tarih formatı
                duration = 1
            )
        }

        assertEquals("Invalid date format, use ISO-8601", exception.message)
    }
    @Test
    fun `get rentals for user with no rentals`() {
        val user = UserServices.addUser("Test User", "test@example.com")

        val rentals = RentalsDataMem.getRentalsForUser(user.userId)
        assertTrue(rentals.isEmpty()) // Kullanıcının kiralaması yok, boş liste dönecek
    }

    @Test
    fun `get rentals for club and court with no rentals`() {
        val user = UserServices.addUser("Test User", "test@example.com")
        val club = ClubServices.addClub("Test Club", user.userId)
        val court = CourtServices.addCourt("Test Court", club.clubID)

        val rentals = RentalsDataMem.getRentalsForClubAndCourt(club.clubID, court.courtID, null)
        assertTrue(rentals.isEmpty()) // Klüp ve kort için kiralama yapılmamış, boş liste dönecek
    }
    @Test
    fun `get rental by invalid ID`() {
        val rental = RentalServices.getRentalById("invalid-id")
        assertNull(rental) // Geçersiz ID, sonuç null olmalı
    }




}
