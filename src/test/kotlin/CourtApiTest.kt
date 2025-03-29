package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.UserServices
import services.ClubServices
import services.CourtServices
import storage.UsersDataMem
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem

class CourtTests {

    @BeforeEach
    fun setup() {
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }



    @Test
    fun `create court for existing club`() {
        // Create user and club first
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)

        val court = CourtServices.addCourt("Court 1", club.clubID)

        assertNotNull(court.courtID)
        assertEquals("Court 1", court.name)
        assertEquals(club.clubID, court.clubId)
    }

    @Test
    fun `cannot create court for non-existent club`() {
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("Phantom Court", "non-existent-club-id")
        }

        assertEquals("Club ID not found", exception.message)
    }



    @Test
    fun `list all entities`() {
        // Create multiple test entities
        val user1 = UserServices.addUser("User 1", "user1@example.com")
        val user2 = UserServices.addUser("User 2", "user2@example.com")

        val club1 = ClubServices.addClub("Club 1", user1.userID)
        val club2 = ClubServices.addClub("Club 2", user2.userID)

        val court1 = CourtServices.addCourt("Court 1", club1.clubID)
        val court2 = CourtServices.addCourt("Court 2", club2.clubID)

        // Test list methods
        assertEquals(2, UserServices.getUsers().size)
        assertEquals(2, ClubServices.getClubs().size)
        assertEquals(2, CourtServices.getCourts().size)
    }
}