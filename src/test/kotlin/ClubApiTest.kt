package tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.UserServices

class ClubTest {

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
    fun `create club with manual userId`() {
        val userId = 10
        val user = UserServices.CreateUser(userId, "Club Owner", "owner@example.com")
        assertEquals(userId, user.userId)

        val clubId = 20
        val club = ClubServices.addClub(clubId, "Tennis Club", user.userId)
        assertEquals(clubId, club.clubID)
        assertEquals("Tennis Club", club.name)
        assertEquals(user.userId, club.userID)
    }

    @Test
    fun `cannot create club with non-existent user`() {
        val clubId = 5
        val userID = 999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub(clubId, "Phantom Club", userID)
        }
        assertEquals("User ID '999' not found", exception.message)
    }

    @Test
    fun `create multiple clubs with the same user and manual IDs`() {
        val userId = 30
        val user = UserServices.CreateUser(userId, "Club Owner", "owner2@example.com")

        val club1Id = 101
        val club2Id = 102
        val club1 = ClubServices.addClub(club1Id, "Tennis Club", user.userId)
        val club2 = ClubServices.addClub(club2Id, "Football Club", user.userId)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" && it.clubID == club1Id })
        assertTrue(clubs.any { it.name == "Football Club" && it.clubID == club2Id })
    }

    @Test
    fun `cannot create club with empty name`() {
        val userId = 40
        val user = UserServices.CreateUser(userId, "Club Owner", "owner3@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub(55, "", user.userId)
        }
        assertEquals("Club name cannot be empty", exception.message)
    }

    @Test
    fun `cannot create club with name exceeding max length`() {
        val userId = 50
        val user = UserServices.CreateUser(userId, "Club Owner", "owner4@example.com")
        val longName = "A".repeat(101)
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub(56, longName, user.userId)
        }
        assertEquals("Club name cannot exceed 100 characters", exception.message)
    }

    @Test
    fun `list all clubs`() {
        val userId = 60
        val user = UserServices.CreateUser(userId, "Club Owner", "owner5@example.com")
        ClubServices.addClub(200, "Tennis Club", user.userId)
        ClubServices.addClub(201, "Football Club", user.userId)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `verify club owner`() {
        val userId = 70
        val user = UserServices.CreateUser(userId, "Club Owner", "owner6@example.com")
        val clubId = 300
        val club = ClubServices.addClub(clubId, "Tennis Club", user.userId)
        assertEquals(user.userId, club.userID)
    }

    @Test
    fun `get club by valid id`() {
        val userId = 80
        val user = UserServices.CreateUser(userId, "Club Owner", "owner7@example.com")
        val clubId = 400
        val created = ClubServices.addClub(clubId, "Basketball Club", user.userId)
        val foundClub = ClubServices.getClubById(clubId)

        assertNotNull(foundClub)
        assertEquals(created.clubID, foundClub?.clubID)
        assertEquals("Basketball Club", foundClub?.name)
    }

    @Test
    fun `should throw when getting club with invalid id`() {
        val invalidID = 9999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubById(invalidID)
        }
        assertEquals("Club ID '9999' not found", exception.message)
    }

    @Test
    fun `get club details by valid id`() {
        val userId = 90
        val user = UserServices.CreateUser(userId, "Club Owner", "owner8@example.com")
        val clubId = 500
        val created = ClubServices.addClub(clubId, "Chess Club", user.userId)
        val details = ClubServices.getClubDetails(clubId)

        assertNotNull(details)
        assertEquals(created.clubID, details?.clubID)
        assertEquals("Chess Club", details?.name)
        assertEquals(user.userId, details?.userID)
    }

    @Test
    fun `should throw when getting club details with invalid id`() {
        val invalidID = 9999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubDetails(invalidID)
        }
        assertEquals("Club ID '9999' not found", exception.message)
    }

    @Test
    fun `can create clubs with duplicate names`() {
        val userId = 100
        val user = UserServices.CreateUser(userId, "Club Owner", "owner9@example.com")
        val club1 = ClubServices.addClub(600, "Duplicate Club", user.userId)
        val club2 = ClubServices.addClub(601, "Duplicate Club", user.userId)

        assertNotEquals(club1.clubID, club2.clubID)
    }

    @Test
    fun `getClubs returns empty list when no clubs exist`() {
        val clubs = ClubServices.getAllClubs()
        assertTrue(clubs.isEmpty())
    }
}
