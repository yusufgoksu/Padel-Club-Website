import api.*
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.routes
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

fun main() {
    val app = routes(
        homeWebApi(),
        clubsWebApi(),
        courtsWebApi(),
        rentalsWebApi(),
        usersWebApi()
    )

    val server = app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")

    // Test verisi
    val user = UsersDataMem.addUser(name = "yusuf", email = "mert@example.com")
    val club = ClubsDataMem.addClub(name = "Club X", ownerId = user.userID)
    val court = CourtsDataMem.addCourt(name = "Court 1", clubId = club.clubID)

    val rental = RentalsDataMem.addRental(
        userId = user.userID,
        courtId = court.courtID,
        startTime = "2025-03-27T14:00:00Z",
        duration = 2,
        clubID = club.clubID
    )
}
