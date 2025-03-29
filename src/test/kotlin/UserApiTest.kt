import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.http4k.client.OkHttp
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.json
import org.http4k.core.Body
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserTest {
    private val client = OkHttp()
    private val baseUrl = "http://localhost:900/users"

    @Serializable
    data class User(val name: String, val email: String)

    @Test
    fun `should create a new user`() {
        val user = User("John Doe", "john.doe@example.com")
        val request = Request(Method.POST, "$baseUrl")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(user))
        val response: Response = client(request)

        assertEquals(Status.OK, response.status)
        assertTrue(response.bodyString().contains("uid"))
    }

    @Test
    fun `should get user details`() {
        val request = Request(Method.GET, "$baseUrl/1")
        val response: Response = client(request)

        assertEquals(Status.OK, response.status)
        assertTrue(response.bodyString().contains("John Doe"))
    }

    @Test
    fun `should not create duplicate user with same email`() {
        val user = User("Jane Doe", "jane.doe@example.com")
        val request = Request(Method.POST, "$baseUrl")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(user))
        client(request) // First request (should succeed)

        val response: Response = client(request) // Second request (should fail)

        assertEquals(Status.BAD_REQUEST, response.status)
    }
}
