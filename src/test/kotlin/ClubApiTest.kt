package tests

import kotlinx.serialization.Serializable
import org.http4k.client.OkHttp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.UserServices

class ClubTest {
    private val client = OkHttp()
    private val baseUrl = "http://localhost:9000/users"

    @Serializable
    data class Club(val cid: String, val name: String, val ownerUid: String)

    @Test
    fun `create club with valid user`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Create the club using the valid user's UID
        val club = ClubServices.addClub("Tennis Club", user.userID)

        // Assertions to ensure the club has been created correctly
        assertNotNull(club.clubID)
        assertEquals("Tennis Club", club.name)
        assertEquals(user.userID, club.ownerUid)
    }

    @Test
    fun `cannot create club with non-existent user`() {
        // Attempt to create a club with a non-existent user UID
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("Phantom Club", "non-existent-uid")
        }

        // Check the exception message
        assertEquals("Owner UID not found", exception.message)
    }

    @Test
    fun `create multiple clubs with the same user`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Create the first club
        val club1 = ClubServices.addClub("Tennis Club", user.userID)
        assertNotNull(club1.clubID)
        assertEquals("Tennis Club", club1.name)
        assertEquals(user.userID, club1.ownerUid)

        // Create the second club
        val club2 = ClubServices.addClub("Football Club", user.userID)
        assertNotNull(club2.clubID)
        assertEquals("Football Club", club2.name)
        assertEquals(user.userID, club2.ownerUid)

        // Ensure both clubs were created successfully
        assertEquals(2, ClubServices.getClubs().size)
    }

    @Test
    fun `cannot create club with empty name`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Attempt to create a club with an empty name
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("", user.userID)
        }

        // Check the exception message
        assertEquals("Club name cannot be empty", exception.message)
    }

    @Test
    fun `cannot create club with name exceeding max length`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Attempt to create a club with a name exceeding max length
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("A".repeat(101), user.userID) // 101 characters
        }

        // Check the exception message
        assertEquals("Club name cannot exceed 100 characters", exception.message)
    }

    @Test
    fun `list all clubs`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Create multiple clubs
        ClubServices.addClub("Tennis Club", user.userID)
        ClubServices.addClub("Football Club", user.userID)

        // Test that all clubs are listed
        val clubs = ClubServices.getClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `verify club owner`() {
        // First create a user
        val user = UserServices.addUser("Club Owner", "owner@example.com")

        // Create a club
        val club = ClubServices.addClub("Tennis Club", user.userID)

        // Verify that the club's owner is correctly assigned
        assertEquals(user.userID, club.ownerUid)
    }
}
