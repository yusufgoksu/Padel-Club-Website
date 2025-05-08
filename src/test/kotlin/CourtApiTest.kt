package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.CourtServices
import services.ClubServices
import services.UserServices
import storage.UsersDataMem
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem



class CourtTests {

    @BeforeEach
    fun setup() {
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
        UsersDataMem.idCounter.set(1)
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()

    }

    @Test
    fun `create court for existing club`() {
        // Create user and club first
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val court = CourtServices.addCourt("Court 1", club.clubID)

        assertNotNull(court.courtID)
        assertEquals("Court 1", court.name)
        assertEquals(club.clubID, court.clubId)
    }

    @Test
    fun `cannot create court for non-existent club`() {
        val invalidClubId = 999

        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("Phantom Court", invalidClubId)
        }

        assertEquals("Club ID '999' not found", ex.message)
    }

    @Test
    fun `cannot create court with empty name`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("", club.clubID)
        }

        assertEquals("Court name cannot be empty", ex.message)
    }

    @Test
    fun `cannot create court with name exceeding max length`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val longName = "X".repeat(101)
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt(longName, club.clubID)
        }

        assertEquals("Court name cannot exceed 100 characters", ex.message)
    }

    @Test
    fun `create multiple courts for the same club`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val court1 = CourtServices.addCourt("Court A", club.clubID)
        val court2 = CourtServices.addCourt("Court B", club.clubID)

        val all = CourtServices.getAllCourts()
        assertEquals(2, all.size)
        assertTrue(all.any { it.name == "Court A" })
        assertTrue(all.any { it.name == "Court B" })
    }

    @Test
    fun `get court by name`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val court = CourtServices.addCourt("Court X", club.clubID)
        val found = CourtServices.getCourtByName("Court X")

        assertNotNull(found)
        assertEquals(court.courtID, found?.courtID)
    }

    @Test
    fun `get court by non-existent name returns null`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val found = CourtServices.getCourtByName("NoSuchCourt")
        assertNull(found)
    }

    @Test
    fun `get court by invalid id throws exception`() {
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.getCourtById(999)
        }
        assertEquals("Court ID '999' not found", ex.message)
    }

    @Test
    fun `get courts for invalid club id throws exception`() {
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.getCourtsForClub(999)
        }
        assertEquals("Club ID '999' not found", ex.message)
    }
}
