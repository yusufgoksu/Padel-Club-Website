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
        val club = ClubServices.addClub("Tennis Club", user.userId)

        val court = CourtServices.addCourt("Court 1", club.clubID)

        assertNotNull(court.courtID)
        assertEquals("Court 1", court.name)
        assertEquals(club.clubID, court.clubId)
    }

    @Test
    fun `cannot create court for non-existent club`() {
        // Öncelikle geçerli bir kulüp ekleyelim
        val clubID = "non-existent-club-id"

        // Geçerli kulüp ID'si kullanılarak kort eklemeye çalışılmalı
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("Phantom Court", clubID)
        }

        assertEquals("Club ID '$clubID' not found" , exception.message)
    }

    @Test
    fun `cannot create court with empty name`() {
        // First create a user and a club
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        // Attempt to create a court with an empty name
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("", club.clubID)
        }

        // Check the exception message
        assertEquals("Court name cannot be empty", exception.message)
    }

    @Test
    fun `cannot create court with name exceeding max length`() {
        // First create a user and a club
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        // Attempt to create a court with a name exceeding max length
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("A".repeat(101), club.clubID) // 101 characters
        }

        // Check the exception message
        assertEquals("Court name cannot exceed 100 characters", exception.message)
    }

    @Test
    fun `create multiple courts for the same club`() {
        // First create a user and a club
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        // Create multiple courts
        val court1 = CourtServices.addCourt("Court 1", club.clubID)
        val court2 = CourtServices.addCourt("Court 2", club.clubID)

        // Ensure both courts were created successfully
        assertNotNull(court1.courtID)
        assertNotNull(court2.courtID)
        assertEquals(2, CourtServices.getAllCourts().size)
    }

    @Test
    fun `get court by name`() {
        // First create a user and a club
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        // Create a court
        val court = CourtServices.addCourt("Court 1", club.clubID)

        // Get court by name
        val retrievedCourt = CourtServices.getCourtByName("Court 1")

        // Ensure the court was retrieved correctly
        assertNotNull(retrievedCourt)
        assertEquals("Court 1", retrievedCourt?.name)
    }

    @Test
    fun `cannot get court by non-existent name`() {
        // First create a user and a club
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        // Try to retrieve a non-existent court by name
        val retrievedCourt = CourtServices.getCourtByName("Non-existent Court")

        // Ensure no court is returned
        assertNull(retrievedCourt)
    }
    @Test
    fun `get court by invalid id returns exception`() {
        // Geçersiz ID ile kort arandı ve IllegalArgumentException fırlatılmalı
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.getCourtById("non-existent-id")
        }

        // İstediğimiz hatanın mesajını kontrol et
        assertEquals("Court ID 'non-existent-id' not found", exception.message)
    }

    @Test
    fun `get court details by invalid id returns exception`() {
        // Geçersiz ID ile kort detayları için sorgulama yapılmalı ve IllegalArgumentException fırlatılmalı
        val exception = assertThrows<IllegalArgumentException> {
            CourtServices.getCourtById("non-existent-id")
        }

        // İstediğimiz hatanın mesajını kontrol et
        assertEquals("Court ID 'non-existent-id' not found", exception.message)
    }



}


