package tests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.ClubServices
import services.UserServices

class ClubTests {

    @BeforeEach
    fun setup() {
        Database.getConnection().use { conn ->
            conn.prepareStatement("DELETE FROM rentals;").executeUpdate()
            conn.prepareStatement("DELETE FROM courts;").executeUpdate()
            conn.prepareStatement("DELETE FROM clubs;").executeUpdate()
            conn.prepareStatement("DELETE FROM users;").executeUpdate()
        }
    }

    /* ---------- 1. OLUŞTURMA ---------- */

    @Test
    fun `create club and auto-generate IDs`() {
        val owner = UserServices.createUser("Club Owner", "owner@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)

        assertNotNull(owner.userId)
        assertNotNull(club.clubID)
        assertEquals("Tennis Club", club.name)
        assertEquals(owner.userId, club.userID)
    }

    /* ---------- 2. GEÇERSİZ KULLANICI ---------- */

    @Test
    fun `cannot create club with non-existent user`() {
        val ex = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("Phantom Club", 999)
        }
        assertEquals("User ID '999' not found", ex.message)
    }

    /* ---------- 3. AYNI KULLANICI İLE BİRDEN FAZLA KULÜP ---------- */

    @Test
    fun `create multiple clubs with the same user`() {
        val owner = UserServices.createUser("Club Owner", "owner2@example.com")

        val club1 = ClubServices.addClub("Tennis Club",   owner.userId!!)
        val club2 = ClubServices.addClub("Football Club", owner.userId!!)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club"   && it.clubID == club1.clubID })
        assertTrue(clubs.any { it.name == "Football Club" && it.clubID == club2.clubID })
    }

    /* ---------- 4-5. BOŞ VE ÇOK UZUN İSİM ---------- */

    @Test
    fun `cannot create club with empty name`() {
        val owner = UserServices.createUser("Club Owner", "owner3@example.com")
        val ex = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("", owner.userId!!)
        }
        assertEquals("Club name cannot be empty", ex.message)
    }

    @Test
    fun `cannot create club with name exceeding max length`() {
        val owner = UserServices.createUser("Club Owner", "owner4@example.com")
        val longName = "A".repeat(101)
        val ex = assertThrows<IllegalArgumentException> {
            ClubServices.addClub(longName, owner.userId!!)
        }
        assertEquals("Club name cannot exceed 100 characters", ex.message)
    }

    /* ---------- 6. TÜM KULÜPLERİ LİSTELE ---------- */

    @Test
    fun `list all clubs`() {
        val owner = UserServices.createUser("Club Owner", "owner5@example.com")
        ClubServices.addClub("Tennis Club",   owner.userId!!)
        ClubServices.addClub("Football Club", owner.userId!!)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    /* ---------- 7. SAHİPLİK DOĞRULAMA ---------- */

    @Test
    fun `verify club owner`() {
        val owner = UserServices.createUser("Club Owner", "owner6@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)
        assertEquals(owner.userId, club.userID)
    }

    /* ---------- 8-9. ID İLE GETİRME ---------- */

    @Test
    fun `get club by valid id`() {
        val owner = UserServices.createUser("Club Owner", "owner7@example.com")
        val created = ClubServices.addClub("Basketball Club", owner.userId!!)
        val found   = ClubServices.getClubById(created.clubID!!)

        assertNotNull(found)
        assertEquals(created.clubID, found?.clubID)
        assertEquals("Basketball Club", found?.name)
    }

    @Test
    fun `should throw when getting club with invalid id`() {
        val ex = assertThrows<IllegalArgumentException> {
            ClubServices.getClubById(9999)
        }
        assertEquals("Club ID '9999' not found", ex.message)
    }

    /* ---------- 10-11. DETAY GETİRME ---------- */

    @Test
    fun `get club details by valid id`() {
        val owner = UserServices.createUser("Club Owner", "owner8@example.com")
        val created = ClubServices.addClub("Chess Club", owner.userId!!)
        val details = ClubServices.getClubDetails(created.clubID!!)

        assertNotNull(details)
        assertEquals(created.clubID, details?.clubID)
        assertEquals("Chess Club",   details?.name)
        assertEquals(owner.userId,   details?.userID)
    }

    @Test
    fun `should throw when getting club details with invalid id`() {
        val ex = assertThrows<IllegalArgumentException> {
            ClubServices.getClubDetails(9999)
        }
        assertEquals("Club ID '9999' not found", ex.message)
    }

    /* ---------- 12. AYNI İSİMLE FARKLI KULÜPLER ---------- */

    @Test
    fun `can create clubs with duplicate names`() {
        val owner = UserServices.createUser("Club Owner", "owner9@example.com")
        val club1 = ClubServices.addClub("Duplicate Club", owner.userId!!)
        val club2 = ClubServices.addClub("Duplicate Club", owner.userId!!)

        assertNotEquals(club1.clubID, club2.clubID)
    }

    /* ---------- 13. BOŞ TABLO ---------- */

    @Test
    fun `getClubs returns empty list when no clubs exist`() {
        val clubs = ClubServices.getAllClubs()
        assertTrue(clubs.isEmpty())
    }
}
