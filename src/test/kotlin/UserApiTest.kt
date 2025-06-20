package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.UserServices

class UserTests {

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

    /* ---------- 1. OLUŞTURMA TESTİ ---------- */

    @Test
    fun `should create user and auto-generate ID`() {
        val user = UserServices.createUser("John Doe", "john.doe@example.com")

        assertNotNull(user.userId)                 // ID veritabanı tarafından atandı mı?
        assertEquals("John Doe", user.name)
        assertEquals("john.doe@example.com", user.email)
    }

    /* ---------- 2. E-POSTA TEKFARLAMA ---------- */

    @Test
    fun `should not allow duplicate email`() {
        UserServices.createUser("John Doe", "john.doe@example.com")

        val exception = assertThrows<IllegalArgumentException> {
            UserServices.createUser("Jane Doe", "john.doe@example.com")
        }
        assertEquals("Email already exists", exception.message)
    }

    /* ---------- 3-4. ID İLE GETİRME ---------- */

    @Test
    fun `should get user by valid ID`() {
        val user = UserServices.createUser("John Doe", "john.doe@example.com")
        val retrieved = UserServices.getUserById(user.userId!!)

        assertNotNull(retrieved)
        assertEquals(user.userId, retrieved?.userId)
        assertEquals(user.name,   retrieved?.name)
        assertEquals(user.email,  retrieved?.email)
    }

    @Test
    fun `should return null for invalid ID`() {
        val retrieved = UserServices.getUserById(999)
        assertNull(retrieved)
    }

    /* ---------- 5. LİSTELEME ---------- */

    @Test
    fun `should list all users`() {
        UserServices.createUser("User 1", "user1@example.com")
        UserServices.createUser("User 2", "user2@example.com")

        val list = UserServices.getAllUsers()
        assertEquals(2, list.size)
    }

    /* ---------- 6-7. E-POSTA İLE GETİRME ---------- */

    @Test
    fun `should get user by email`() {
        val user = UserServices.createUser("Alice", "alice@example.com")
        val found = UserServices.getUserByEmail("alice@example.com")

        assertNotNull(found)
        assertEquals(user.userId, found?.userId)
    }

    @Test
    fun `should return null for invalid email`() {
        val found = UserServices.getUserByEmail("notfound@example.com")
        assertNull(found)
    }

    /* ---------- 8-9. TOKEN ÜRETİMİ ---------- */

    @Test
    fun `should generate token for valid user`() {
        val user = UserServices.createUser("Bob", "bob@example.com")
        val tokenPair = UserServices.generateUserToken(user.userId!!)

        assertNotNull(tokenPair)
        assertEquals(user.userId, tokenPair?.second)  // (token, userId)
        assertTrue(tokenPair?.first?.isNotBlank() == true)
    }

    @Test
    fun `should return null when generating token for invalid user`() {
        val tokenPair = UserServices.generateUserToken(999)
        assertNull(tokenPair)
    }
}
