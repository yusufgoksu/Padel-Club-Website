package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.UserServices
import storage.UsersDataMem

class UserTests {

    @BeforeEach
    fun setup() {
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
    }

    @Test
    fun `create user with valid data`() {
        // Test creating a user with valid data
        val user = UserServices.addUser("John Doe", "john.doe@example.com")

        // Verify that the user was created successfully
        assertNotNull(user.userID)
        assertEquals("John Doe", user.name)
        assertEquals("john.doe@example.com", user.email)
    }

    @Test
    fun `cannot create user with duplicate email`() {
        // Test creating the first user
        UserServices.addUser("John Doe", "john.doe@example.com")

        // Try to create a second user with the same email
        val exception = assertThrows<IllegalArgumentException> {
            UserServices.addUser("Jane Doe", "john.doe@example.com")
        }

        // Verify that the exception is thrown due to duplicate email
        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `get user by valid ID`() {
        // Create a user
        val user = UserServices.addUser("John Doe", "john.doe@example.com")

        // Try to retrieve the user by their ID
        val retrievedUser = UserServices.getUserById(user.userID)

        // Verify that the correct user is retrieved
        assertNotNull(retrievedUser)
        assertEquals(user.userID, retrievedUser?.userID)
        assertEquals(user.name, retrievedUser?.name)
        assertEquals(user.email, retrievedUser?.email)
    }

    @Test
    fun `cannot get user by invalid ID`() {
        // Try to get a user with an invalid ID
        val invalidUserId = "non-existent-user-id"
        val user = UserServices.getUserById(invalidUserId)

        // Verify that the result is null for an invalid ID
        assertNull(user)
    }

    @Test
    fun `list all users`() {
        // Create multiple users
        UserServices.addUser("User 1", "user1@example.com")
        UserServices.addUser("User 2", "user2@example.com")

        // Retrieve all users and verify the list size
        val users = UserServices.getUsers()

        // Verify that the list contains 2 users
        assertEquals(2, users.size)
    }
}
