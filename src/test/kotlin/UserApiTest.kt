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

    @Test
    fun `should create user with manual userId`() {
        val userId = 10
        val user = UserServices.CreateUser(userId, "John Doe", "john.doe@example.com")

        assertEquals(userId, user.userId)
        assertEquals("John Doe", user.name)
        assertEquals("john.doe@example.com", user.email)
    }

    @Test
    fun `should not allow duplicate email`() {
        UserServices.CreateUser(11, "John Doe", "john.doe@example.com")

        val exception = assertThrows<IllegalArgumentException> {
            UserServices.CreateUser(12, "Jane Doe", "john.doe@example.com")
        }

        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `should get user by valid ID`() {
        val userId = 13
        val user = UserServices.CreateUser(userId, "John Doe", "john.doe@example.com")
        val retrieved = UserServices.getUserById(user.userId)

        assertNotNull(retrieved)
        assertEquals(user.userId, retrieved?.userId)
        assertEquals(user.name, retrieved?.name)
        assertEquals(user.email, retrieved?.email)
    }

    @Test
    fun `should return null for invalid ID`() {
        val retrieved = UserServices.getUserById(999)
        assertNull(retrieved)
    }

    @Test
    fun `should list all users`() {
        UserServices.CreateUser(14, "User 1", "user1@example.com")
        UserServices.CreateUser(15, "User 2", "user2@example.com")

        val list = UserServices.getAllUsers()
        assertEquals(2, list.size)
    }

    @Test
    fun `should get user by email`() {
        val userId = 16
        val user = UserServices.CreateUser(userId, "Alice", "alice@example.com")
        val found = UserServices.getUserByEmail("alice@example.com")

        assertNotNull(found)
        assertEquals(user.userId, found?.userId)
    }

    @Test
    fun `should return null for invalid email`() {
        val found = UserServices.getUserByEmail("notfound@example.com")
        assertNull(found)
    }

    @Test
    fun `should generate token for valid user`() {
        val userId = 17
        val user = UserServices.CreateUser(userId, "Bob", "bob@example.com")
        val tokenPair = UserServices.generateUserToken(user.userId)

        assertNotNull(tokenPair)
        assertEquals(user.userId, tokenPair?.second)
        assertTrue(tokenPair?.first?.isNotBlank() == true)
    }

    @Test
    fun `should return null when generating token for invalid user`() {
        val tokenPair = UserServices.generateUserToken(999)
        assertNull(tokenPair)
    }
}
