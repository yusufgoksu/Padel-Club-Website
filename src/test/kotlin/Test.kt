package tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.CourtServices
import services.ClubServices
import services.UserServices
import services.RentalServices
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import java.time.LocalDateTime

class GeneralTest {

    @BeforeEach
    fun setup() {
        // Clean up in-memory storage before each test
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }

    // Test: Create two users
    @Test
    fun `create two users`() {
        val user1 = UserServices.addUser("John Doe", "john.doe@example.com")
        val user2 = UserServices.addUser("Jane Smith", "jane.smith@example.com")

        assertNotNull(user1)
        assertNotNull(user2)
        assertEquals("john.doe@example.com", user1.email)
        assertEquals("jane.smith@example.com", user2.email)
    }

    // Test: Try to create another user with the same email of the first
    @Test
    fun `cannot create user with duplicate email`() {
        UserServices.addUser("John Doe", "john.doe@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            UserServices.addUser("Jane Doe", "john.doe@example.com")
        }
        assertEquals("Email already exists", exception.message)
    }

    // Test: Create two clubs
    @Test
    fun `create two clubs`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club1 = ClubServices.addClub("Tennis Club", user.userId)
        val club2 = ClubServices.addClub("Football Club", user.userId)

        assertNotNull(club1)
        assertNotNull(club2)
        assertEquals("Tennis Club", club1.name)
        assertEquals("Football Club", club2.name)
    }

    // Test: Add two courts to the first club
    @Test
    fun `add two courts to the first club`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val court1 = CourtServices.addCourt("Padel Court", club.clubID)
        val court2 = CourtServices.addCourt("Tennis Court", club.clubID)

        val courts = ClubServices.getAllClubs()
        assertEquals(1, courts.size)
        assertTrue(courts.any { it.name == "Padel Court" })
        assertTrue(courts.any { it.name == "Tennis Court" })
    }

    // Test: List all courts of the two clubs
    @Test
    fun `list all courts of the two clubs`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club1 = ClubServices.addClub("Tennis Club", user.userId)
        val club2 = ClubServices.addClub("Football Club", user.userId)

        CourtServices.addCourt("Padel Court", club1.clubID)
        CourtServices.addCourt("Tennis Court", club1.clubID)
        CourtServices.addCourt("Football Court", club2.clubID)

        val allCourts = CourtServices.getAllCourts()
        assertEquals(3, allCourts.size)
        assertTrue(allCourts.any { it.name == "Padel Court" })
        assertTrue(allCourts.any { it.name == "Tennis Court" })
        assertTrue(allCourts.any { it.name == "Football Court" })
    }

    // Test: List the available hours for the first court today
    @Test
    fun `list available hours for the first court today`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Padel Court", club.clubID)

        val availableHours =
            RentalServices.getAvailableHours(club.clubID, court.courtID, LocalDateTime.now().toLocalDate().toString())
        assertTrue(availableHours.isNotEmpty())
    }

    // Test: Create a rental with an invalid date
    @Test
    fun `create rental with invalid date`() {
        val user = UserServices.addUser("John Doe", "john.doe@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Padel Court", club.clubID)

        val invalidDate = LocalDateTime.now().minusDays(1) // Past date
        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(club.clubID, court.courtID, user.userId, invalidDate.toString(), 2)
        }
        assertEquals("Invalid rental date", exception.message)
    }

    // Test: Create three rentals of two hours in the first court today
    @Test
    fun `create three rentals of two hours in the first court today`() {
        val user = UserServices.addUser("John Doe", "john.doe@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Padel Court", club.clubID)

        val rental1 =
            RentalServices.addRental(club.clubID, court.courtID, user.userId, LocalDateTime.now().toString(), 2)
        val rental2 = RentalServices.addRental(
            club.clubID,
            court.courtID,
            user.userId,
            LocalDateTime.now().plusHours(2).toString(),
            2
        )
        val rental3 = RentalServices.addRental(
            club.clubID,
            court.courtID,
            user.userId,
            LocalDateTime.now().plusHours(4).toString(),
            2
        )

        assertNotNull(rental1)
        assertNotNull(rental2)
        assertNotNull(rental3)
    }

    // Test: List the available hours for the first court today after rentals
    @Test
    fun `list available hours for the first court today after rentals`() {
        val user = UserServices.addUser("John Doe", "john.doe@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        val court = CourtServices.addCourt("Padel Court", club.clubID)

        RentalServices.addRental(club.clubID, court.courtID, user.userId, LocalDateTime.now().toString(), 2)
        RentalServices.addRental(
            club.clubID,
            court.courtID,
            user.userId,
            LocalDateTime.now().plusHours(2).toString(),
            2
        )
        RentalServices.addRental(
            club.clubID,
            court.courtID,
            user.userId,
            LocalDateTime.now().plusHours(4).toString(),
            2
        )

        val availableHours =
            RentalServices.getAvailableHours(club.clubID, court.courtID, LocalDateTime.now().toLocalDate().toString())
        assertTrue(availableHours.isNotEmpty())
    }
}