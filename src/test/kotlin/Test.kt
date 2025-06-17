package tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.CourtServices
import services.ClubServices
import services.RentalServices
import services.UserServices
import java.time.LocalDateTime

class GeneralTest {

    @BeforeEach
    fun setup() {
        // Veritabanını sıfırla
        Database.getConnection().use { conn ->
            conn.prepareStatement("DELETE FROM rentals;").executeUpdate()
            conn.prepareStatement("DELETE FROM courts;").executeUpdate()
            conn.prepareStatement("DELETE FROM clubs;").executeUpdate()
            conn.prepareStatement("DELETE FROM users;").executeUpdate()
        }
    }

    @Test
    fun `create two users`() {
        val user1 = UserServices.CreateUser(1, "John Doe", "john.doe@example.com")
        val user2 = UserServices.CreateUser(2, "Jane Smith", "jane.smith@example.com")

        assertNotNull(user1)
        assertNotNull(user2)
        assertEquals("john.doe@example.com", user1.email)
        assertEquals("jane.smith@example.com", user2.email)
    }

    @Test
    fun `cannot create user with duplicate email`() {
        UserServices.CreateUser(1, "John Doe", "john.doe@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            UserServices.CreateUser(2, "Jane Doe", "john.doe@example.com")
        }
        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `create two clubs`() {
        val user = UserServices.CreateUser(1, "Club Owner", "owner@example.com")
        val club1 = ClubServices.addClub(1, "Tennis Club", user.userId)
        val club2 = ClubServices.addClub(2, "Football Club", user.userId)

        assertNotNull(club1)
        assertNotNull(club2)
        assertEquals("Tennis Club", club1.name)
        assertEquals("Football Club", club2.name)
    }

    @Test
    fun `add two courts to the first club`() {
        val user = UserServices.CreateUser(1, "Club Owner", "owner@example.com")
        val club = ClubServices.addClub(1, "Tennis Club", user.userId)

        val court1 = CourtServices.addCourt(1, "Padel Court", club.clubID)
        val court2 = CourtServices.addCourt(2, "Tennis Court", club.clubID)

        val courts = CourtServices.getAllCourts()
        assertEquals(2, courts.size)
        assertTrue(courts.any { it.name == "Padel Court" })
        assertTrue(courts.any { it.name == "Tennis Court" })
    }

    @Test
    fun `list all courts of the two clubs`() {
        val user = UserServices.CreateUser(1, "Club Owner", "owner@example.com")
        val club1 = ClubServices.addClub(1, "Tennis Club", user.userId)
        val club2 = ClubServices.addClub(2, "Football Club", user.userId)

        CourtServices.addCourt(1, "Padel Court", club1.clubID)
        CourtServices.addCourt(2, "Tennis Court", club1.clubID)
        CourtServices.addCourt(3, "Football Court", club2.clubID)

        val allCourts = CourtServices.getAllCourts()
        assertEquals(3, allCourts.size)
        assertTrue(allCourts.any { it.name == "Padel Court" })
        assertTrue(allCourts.any { it.name == "Tennis Court" })
        assertTrue(allCourts.any { it.name == "Football Court" })
    }

    @Test
    fun `list available hours for the first court today`() {
        val user = UserServices.CreateUser(1, "Club Owner", "owner@example.com")
        val club = ClubServices.addClub(1, "Tennis Club", user.userId)
        val court = CourtServices.addCourt(1, "Padel Court", club.clubID)

        val availableHours =
            RentalServices.getAvailableHours(club.clubID, court.courtID, LocalDateTime.now().toLocalDate().toString())
        assertTrue(availableHours.isNotEmpty())
    }

    @Test
    fun `create rental with invalid date`() {
        val user = UserServices.CreateUser(1, "John Doe", "john.doe@example.com")
        val club = ClubServices.addClub(1, "Tennis Club", user.userId)
        val court = CourtServices.addCourt(1, "Padel Court", club.clubID)

        val invalidDate = LocalDateTime.now().minusDays(1) // geçmiş tarih
        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(1, club.clubID, court.courtID, user.userId, invalidDate.toString(), 2)
        }
        assertEquals("Invalid rental date", exception.message)
    }

    @Test
    fun `create three rentals of two hours in the first court today`() {
        val user = UserServices.CreateUser(1, "John Doe", "john.doe@example.com")
        val club = ClubServices.addClub(1, "Tennis Club", user.userId)
        val court = CourtServices.addCourt(1, "Padel Court", club.clubID)

        val rental1 = RentalServices.addRental(1, club.clubID, court.courtID, user.userId, LocalDateTime.now().toString(), 2)
        val rental2 = RentalServices.addRental(2, club.clubID, court.courtID, user.userId, LocalDateTime.now().plusHours(2).toString(), 2)
        val rental3 = RentalServices.addRental(3, club.clubID, court.courtID, user.userId, LocalDateTime.now().plusHours(4).toString(), 2)

        assertNotNull(rental1)
        assertNotNull(rental2)
        assertNotNull(rental3)
    }

    @Test
    fun `list available hours for the first court today after rentals`() {
        val user = UserServices.CreateUser(1, "John Doe", "john.doe@example.com")
        val club = ClubServices.addClub(1, "Tennis Club", user.userId)
        val court = CourtServices.addCourt(1, "Padel Court", club.clubID)

        RentalServices.addRental(1, club.clubID, court.courtID, user.userId, LocalDateTime.now().toString(), 2)
        RentalServices.addRental(2, club.clubID, court.courtID, user.userId, LocalDateTime.now().plusHours(2).toString(), 2)
        RentalServices.addRental(3, club.clubID, court.courtID, user.userId, LocalDateTime.now().plusHours(4).toString(), 2)

        val availableHours =
            RentalServices.getAvailableHours(club.clubID, court.courtID, LocalDateTime.now().toLocalDate().toString())
        assertTrue(availableHours.isNotEmpty())
    }
}
