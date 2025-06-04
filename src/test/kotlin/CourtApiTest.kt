package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.CourtServices
import services.ClubServices
import services.UserServices

class CourtTests {

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
    fun `create court for existing club`() {
        val userId = 1
        val clubId = 1
        val courtId = 1

        val user = UserServices.addUser(userId, "Club Owner", "owner@example.com")
        val club = ClubServices.addClub(clubId, "Tennis Club", user.userId)
        val court = CourtServices.addCourt(courtId, "Court 1", club.clubID)

        assertEquals(courtId, court.courtID)
        assertEquals("Court 1", court.name)
        assertEquals(club.clubID, court.clubId)
    }

    @Test
    fun `cannot create court for non-existent club`() {
        val invalidClubId = 999
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt(2, "Phantom Court", invalidClubId)
        }
        assertEquals("Club ID '999' not found", ex.message)
    }

    @Test
    fun `cannot create court with empty name`() {
        val user = UserServices.addUser(3, "Club Owner", "owner2@example.com")
        val club = ClubServices.addClub(3, "Tennis Club", user.userId)

        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt(3, "", club.clubID)
        }
        assertEquals("Court name cannot be empty", ex.message)
    }

    @Test
    fun `cannot create court with name exceeding max length`() {
        val user = UserServices.addUser(4, "Club Owner", "owner3@example.com")
        val club = ClubServices.addClub(4, "Tennis Club", user.userId)

        val longName = "X".repeat(101)
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt(4, longName, club.clubID)
        }
        assertEquals("Court name cannot exceed 100 characters", ex.message)
    }

    @Test
    fun `create multiple courts for the same club`() {
        val user = UserServices.addUser(5, "Club Owner", "owner4@example.com")
        val club = ClubServices.addClub(5, "Tennis Club", user.userId)

        val court1 = CourtServices.addCourt(5, "Court A", club.clubID)
        val court2 = CourtServices.addCourt(6, "Court B", club.clubID)

        val all = CourtServices.getAllCourts()
        assertEquals(2, all.size)
        assertTrue(all.any { it.name == "Court A" && it.courtID == 5 })
        assertTrue(all.any { it.name == "Court B" && it.courtID == 6 })
    }

    // Diğer testleri de benzer şekilde manuel ID ile yapabilirsin
}
