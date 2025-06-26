package tests

import Database                              // default-package’teki singleton
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

    /* ---------- 1. OLUŞTURMA ---------- */

    @Test
    fun `create court for existing club`() {
        val owner = UserServices.createUser("Club Owner", "owner@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)

        val court = CourtServices.addCourt("Court 1", club.clubID!!)

        assertNotNull(court.courtID)
        assertEquals("Court 1", court.name)
        assertEquals(club.clubID, court.clubId)
    }

    /* ---------- 2. GEÇERSİZ KULÜP ---------- */

    @Test
    fun `cannot create court for non-existent club`() {
        val invalidClubId = 999
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("Phantom Court", invalidClubId)
        }
        assertEquals("Club ID '999' not found", ex.message)
    }

    /* ---------- 3-4. BOŞ VE ÇOK UZUN İSİM ---------- */

    @Test
    fun `cannot create court with empty name`() {
        val owner = UserServices.createUser("Club Owner", "owner2@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)

        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt("", club.clubID!!)
        }
        assertEquals("Court name cannot be empty", ex.message)
    }

    @Test
    fun `cannot create court with name exceeding max length`() {
        val owner = UserServices.createUser("Club Owner", "owner3@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)

        val longName = "X".repeat(101)
        val ex = assertThrows<IllegalArgumentException> {
            CourtServices.addCourt(longName, club.clubID!!)
        }
        assertEquals("Court name cannot exceed 100 characters", ex.message)
    }

    /* ---------- 5. AYNI KULÜPTE BİRDEN FAZLA KORT ---------- */

    /* ---------- 5. AYNI KULÜPTE BİRDEN FAZLA KORT ---------- */

    @Test
    fun `create multiple courts for the same club`() {
        val owner = UserServices.createUser("Club Owner", "owner4@example.com")
        val club  = ClubServices.addClub("Tennis Club", owner.userId!!)

        val court1 = CourtServices.addCourt("Court A", club.clubID!!)
        val court2 = CourtServices.addCourt("Court B", club.clubID!!)

        // getCourtsForClub yerine getAllCourts() kullanmıyoruz
        val courts = CourtServices.getCourtsForClub(club.clubID!!)
        assertEquals(2, courts.size)
        assertTrue(courts.any { it.courtID == court1.courtID && it.name == "Court A" })
        assertTrue(courts.any { it.courtID == court2.courtID && it.name == "Court B" })
        assertNotEquals(court1.courtID, court2.courtID)
    }


    /* ---------- 6. BOŞ TABLO ---------- */

    @Test
    fun `getCourtsForClub returns empty list when no courts exist`() {
        val owner = UserServices.createUser("Club Owner", "owner5@example.com")
        val club  = ClubServices.addClub("Empty Club", owner.userId!!)

        val courts = CourtServices.getCourtsForClub(club.clubID!!)
        assertTrue(courts.isEmpty())
    }

}
