package tests
import kotlinx.serialization.Serializable
import org.http4k.client.OkHttp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
}
