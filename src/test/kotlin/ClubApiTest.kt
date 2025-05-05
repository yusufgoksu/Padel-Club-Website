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
        // Her testten önce bellek verilerini temizle
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
    }

    @Test
    fun `create club with valid user`() {
        // Geçerli bir kullanıcı ile kulüp oluşturma testi
        val user = UserServices.addUser("Club Owner", "yusufasar@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        assertNotNull(club.clubID)
        assertEquals("Tennis Club", club.name)
        assertEquals(user.userID, club.ownerUid)
    }

    @Test
    fun `cannot create club with non-existent user`() {
        // Var olmayan kullanıcı ile kulüp oluşturulamaz
        val userID = "non-existent-userid"
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("Phantom Club", userID)
        }
        assertEquals(" UserID $userID'not found", exception.message)
    }

    @Test
    fun `create multiple clubs with the same user`() {
        // Aynı kullanıcı ile birden fazla kulüp oluşturulabilir mi?
        val user = UserServices.addUser("Club Owner", "owner1@example.com")
        val club1 = ClubServices.addClub("Tennis Club", user.userID)
        val club2 = ClubServices.addClub("Football Club", user.userID)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `cannot create club with empty name`() {
        // Kulüp adı boş olamaz
        val user = UserServices.addUser("Club Owner", "owner2@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("", user.userID)
        }
        assertEquals("Club name cannot be empty", exception.message)
    }

    @Test
    fun `cannot create club with name exceeding max length`() {
        // Kulüp adı 100 karakteri geçemez
        val user = UserServices.addUser("Club Owner", "owner3@example.com")
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.addClub("A".repeat(101), user.userID)
        }
        assertEquals("Club name cannot exceed 100 characters", exception.message)
    }

    @Test
    fun `list all clubs`() {
        // Tüm kulüplerin listelendiğinden emin ol
        val user = UserServices.addUser("Club Owner", "owner4@example.com")
        ClubServices.addClub("Tennis Club", user.userID)
        ClubServices.addClub("Football Club", user.userID)

        val clubs = ClubServices.getAllClubs()
        assertEquals(2, clubs.size)
        assertTrue(clubs.any { it.name == "Tennis Club" })
        assertTrue(clubs.any { it.name == "Football Club" })
    }

    @Test
    fun `verify club owner`() {
        // Kulübün sahibi doğru atanmış mı kontrol et
        val user = UserServices.addUser("Club Owner", "owner5@example.com")
        val club = ClubServices.addClub("Tennis Club", user.userID)
        assertEquals(user.userID, club.ownerUid)
    }

    @Test
    fun `get club by valid id`() {
        // Geçerli ID ile kulüp bulunabiliyor mu?
        val user = UserServices.addUser("Club Owner", "owner6@example.com")
        val club = ClubServices.addClub("Basketball Club", user.userID)
        val foundClub = ClubServices.getClubById(club.clubID)

        assertNotNull(foundClub)
        assertEquals(club.clubID, foundClub?.clubID)
        assertEquals("Basketball Club", foundClub?.name)
    }

    @Test
    fun `should throw exception when trying to get club with invalid ID`() {
        // Geçersiz bir kulüp ID'si tanımlanır
        val invalidID = "non-existent-id"

        // Bu ID ile kulüp getirilmeye çalışıldığında IllegalArgumentException fırlatılmalıdır
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubById(invalidID)
        }

        // Hata mesajı doğrulanır
        assertEquals("Club ID '$invalidID' not found", exception.message)
    }

    @Test
    fun `get club details by valid id`() {
        // Geçerli ID ile kulüp detayları alınabiliyor mu?
        val user = UserServices.addUser("Club Owner", "owner7@example.com")
        val club = ClubServices.addClub("Chess Club", user.userID)
        val details = ClubServices.getClubDetails(club.clubID)

        assertNotNull(details)
        assertEquals(club.clubID, details?.clubID)
        assertEquals("Chess Club", details?.name)
        assertEquals(user.userID, details?.ownerUid)
    }

    @Test
    fun `should throw exception when trying to get club details with invalid ID`() {
        // Geçersiz bir kulüp ID'si tanımlanır
        val invalidID = "non-existent-id"

        // Bu ID ile kulüp detayları getirilmeye çalışıldığında IllegalArgumentException fırlatılmalıdır
        val exception = assertThrows<IllegalArgumentException> {
            ClubServices.getClubDetails(invalidID)
        }

        // Hata mesajı doğrulanır
        assertEquals("Club ID '$invalidID' not found", exception.message)
    }


    @Test
    fun `can create clubs with duplicate names`() {
        // Aynı isimle birden fazla kulüp oluşturulabiliyor mu?
        val user = UserServices.addUser("Club Owner", "owner8@example.com")
        val club1 = ClubServices.addClub("Duplicate Club", user.userID)
        val club2 = ClubServices.addClub("Duplicate Club", user.userID)

        assertNotNull(club1)
        assertNotNull(club2)
        assertNotEquals(club1.clubID, club2.clubID)
    }

    @Test
    fun `getClubs returns empty list when no clubs exist`() {
        // Hiç kulüp yoksa boş liste dönmeli
        val clubs = ClubServices.getAllClubs()
        assertTrue(clubs.isEmpty())
    }
}
