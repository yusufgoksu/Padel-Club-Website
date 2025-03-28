package tests

import models.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.UserServices
import services.ClubServices
import services.RentalServices
import services.CourtServices
import storage.UsersDataMem
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem

class DomainModelTests {

    @BeforeEach
    fun setup() {
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }

    @Test
    fun `create user with valid data`() {
        val user = User(name = "John Doe", email = "john@example.com")

        assertNotNull(user.uid)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)
    }

    @Test
    fun `create club with valid user`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        val club = ClubServices.addClub("Tennis Club", user.uid)

        assertNotNull(club.cid)
        assertEquals("Tennis Club", club.name)
        assertEquals(user.uid, club.ownerUid)
    }

    @Test
    fun `cannot create club with non-existent user`() {
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("Phantom Club", "non-existent-uid")
        }

        assertEquals("Owner UID not found", exception.message)
    }

    @Test
    fun `create court for existing club`() {
        // Create user and club first
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.uid)

        val court = CourtServices.addCourt("Court 1", club.cid)

        assertNotNull(court.crid)
        assertEquals("Court 1", court.name)
        assertEquals(club.cid, court.clubId)
    }

    @Test
    fun `cannot create court for non-existent club`() {
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("Phantom Court", "non-existent-club-id")
        }

        assertEquals("Club ID not found", exception.message)
    }

    @Test
    fun `create rental with valid data`() {
        // Setup: create user, club, and court
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club =ClubServices.addClub("Tennis Club", user.uid)
        val court = CourtServices.addCourt("Court 1", club.cid)

        val rental = RentalServices.addRental(
            clubId = club.cid,
            courtId = court.crid,
            userId = user.uid,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )

        assertNotNull(rental.rid)
        assertEquals(club.cid, rental.clubId)
        assertEquals(court.crid, rental.courtId)
        assertEquals(user.uid, rental.userId)
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
                userId = user.uid,
                startTime = "2024-07-01T10:00:00",
                duration = 60
            )
        }

        assertEquals("Club ID not found", exception.message)
    }

    @Test
    fun `cannot create rental with invalid court`() {
        val user = UserServices.addUser("Renter", "renter@example.com")
        val club = ClubServices.addClub("Tennis Club", user.uid)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.cid,
                courtId = "non-existent-court",
                userId = user.uid,
                startTime = "2024-07-01T10:00:00",
                duration = 60
            )
        }

        assertEquals("Court ID not found", exception.message)
    }

    @Test
    fun `cannot create rental with invalid user`() {
        val user = UserServices.addUser("Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.uid)
        val court = CourtServices.addCourt("Court 1", club.cid)

        val exception = assertThrows<IllegalArgumentException> {
            RentalServices.addRental(
                clubId = club.cid,
                courtId = court.crid,
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
        val club = ClubServices.addClub("Test Club", user.uid)
        val court = CourtServices.addCourt("Test Court", club.cid)
        val rental = RentalServices.addRental(
            clubId = club.cid,
            courtId = court.crid,
            userId = user.uid,
            startTime = "2024-07-01T10:00:00",
            duration = 60
        )

        // Test retrievals
        assertEquals(user, UserServices.getUserById(user.uid))
        assertEquals(club, ClubServices.getClubById(club.cid))
        assertEquals(court, CourtServices.getCourtById(court.crid))
        assertEquals(rental, RentalServices.getRentalById(rental.rid))
    }

    @Test
    fun `list all entities`() {
        // Create multiple test entities
        val user1 = UserServices.addUser("User 1", "user1@example.com")
        val user2 = UserServices.addUser("User 2", "user2@example.com")

        val club1 = ClubServices.addClub("Club 1", user1.uid)
        val club2 = ClubServices.addClub("Club 2", user2.uid)

        val court1 = CourtServices.addCourt("Court 1", club1.cid)
        val court2 = CourtServices.addCourt("Court 2", club2.cid)

        // Test list methods
        assertEquals(2, UserServices.getUsers().size)
        assertEquals(2, ClubServices.getClubs().size)
        assertEquals(2, CourtServices.getCourts().size)
    }
}