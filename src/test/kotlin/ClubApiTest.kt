package tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.UserServices
import storage.ClubsDataMem
import storage.UsersDataMem

class ClubTest {

    @BeforeEach
    fun setup() {
        // Bellekteki test verilerini temizle
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        ClubsDataMem.idCounter.set(1)
    }

    @Test
    fun `create club with valid user`() {
        val user = UserServices.addUser("Club Owner", "owner@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)

        assertTrue(club.clubID > 0)
        assertEquals("Tennis Club", club.name)
        assertEquals(user.userId, club.userID)
    }

    @Test
    fun `cannot create club with non-existent user`() {
        val userID = 999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("Phantom Club", userID)
        }
        assertEquals("User ID '999' not found", exception.message)
    }

    @Test
    fun `create multiple clubs with the same user`() {
        val user  = UserServices.addUser("Club Owner", "owner1@example.com")
        val club1 = ClubServices.addClub("Tennis Club",   user.userId)
        val club2 = ClubServices.addClub("Football Club", user.userId)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `cannot create club with empty name`() {
        val user = UserServices.addUser("Club Owner", "owner2@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("", user.userId)
        }
        assertEquals("Club name cannot be empty", exception.message)
    }

    @Test
    fun `cannot create club with name exceeding max length`() {
        val user = UserServices.addUser("Club Owner", "owner3@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("A".repeat(101), user.userId)
        }
        assertEquals("Club name cannot exceed 100 characters", exception.message)
    }

    @Test
    fun `list all clubs`() {
        val user = UserServices.addUser("Club Owner", "owner4@example.com")
        ClubServices.addClub("Tennis Club",   user.userId)
        ClubServices.addClub("Football Club", user.userId)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `verify club owner`() {
        val user = UserServices.addUser("Club Owner", "owner5@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userId)
        assertEquals(user.userId, club.userID)
    }

    @Test
    fun `get club by valid id`() {
        val user      = UserServices.addUser("Club Owner", "owner6@example.com")
        val created   = ClubServices.addClub("Basketball Club", user.userId)
        val foundClub = ClubServices.getClubById(created.clubID)

        assertNotNull(foundClub)
        assertEquals(created.clubID, foundClub?.clubID)
        assertEquals("Basketball Club", foundClub?.name)
    }

    @Test
    fun `should throw when getting club with invalid id`() {
        val invalidID = 999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubById(invalidID)
        }
        assertEquals("Club ID '999' not found", exception.message)
    }

    @Test
    fun `get club details by valid id`() {
        val user    = UserServices.addUser("Club Owner", "owner7@example.com")
        val created = ClubServices.addClub("Chess Club", user.userId)
        val details = ClubServices.getClubDetails(created.clubID)

        assertNotNull(details)
        assertEquals(created.clubID, details?.clubID)
        assertEquals("Chess Club", details?.name)
        assertEquals(user.userId, details?.userID)
    }

    @Test
    fun `should throw when getting club details with invalid id`() {
        val invalidID = 999
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubDetails(invalidID)
        }
        assertEquals("Club ID '999' not found", exception.message)
    }

    @Test
    fun `can create clubs with duplicate names`() {
        val user  = UserServices.addUser("Club Owner", "owner8@example.com")
        val club1 = ClubServices.addClub("Duplicate Club", user.userId)
        val club2 = ClubServices.addClub("Duplicate Club", user.userId)

        assertNotEquals(club1.clubID, club2.clubID)
    }

    @Test
    fun `getClubs returns empty list when no clubs exist`() {
        val clubs = ClubServices.getAllClubs()
        assertTrue(clubs.isEmpty())
    }
}
