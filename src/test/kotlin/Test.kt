import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.ClubServices
import services.CourtServices
import services.RentalServices
import services.UserServices
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

class Tests {

    @BeforeEach
    fun setup() {
        // Clear all in-memory storage before each test
        UsersDataMem.users.clear()
        ClubsDataMem.clubs.clear()
        CourtsDataMem.courts.clear()
        RentalsDataMem.rentals.clear()
    }

    @Test
    fun `list all entities`() {
        // Create multiple test entities
        val user1 = UserServices.addUser("User 1", "user1@example.com")
        val user2 = UserServices.addUser("User 2", "user2@example.com")

        val club1 = ClubServices.addClub("Club 1", user1.userID)
        val club2 = ClubServices.addClub("Club 2", user2.userID)

        val court1 = CourtServices.addCourt("Court 1", club1.clubID)
        val court2 = CourtServices.addCourt("Court 2", club2.clubID)

        // Create rentals
        val rental1 = RentalServices.addRental(
            clubId = club1.clubID,
            courtId = court1.courtID,
            userId = user1.userID,
            startTime = "2024-07-01T10:00:00Z",
            duration = 1
        )

        val rental2 = RentalServices.addRental(
            clubId = club2.clubID,
            courtId = court2.courtID,
            userId = user2.userID,
            startTime = "2024-07-02T10:00:00Z",
            duration = 2
        )

        // Test list methods
        assertEquals(2, UserServices.getAllUsers().size)
        assertEquals(2, ClubServices.getAllClubs().size)
        assertEquals(2, CourtServices.getAllCourts().size)
        assertEquals(2, RentalServices.getRentals().size)
    }
}
