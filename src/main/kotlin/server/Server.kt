import api.*
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.routes
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

fun main() {
    // Web API'leri tanımla
    val app = routes(
        homeWebApi(),
        clubsWebApi(),
        courtsWebApi(),
        rentalsWebApi(),
        usersWebApi()
    )

    // Sunucuyu başlat
    val server = app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")

    // 1. Test verisi
    val user1 = UsersDataMem.addUser(name = "Yusuf", email = "yusuf@example.com")
    val club1 = ClubsDataMem.addClub(name = "Padel Club A", ownerId = user1.userID)
    val court1 = CourtsDataMem.addCourt(name = "Court A", clubId = club1.clubID)

    val rental1 = RentalsDataMem.addRental(
        userId = user1.userID,
        courtId = court1.courtID,
        startTime = "2025-03-27T14:00:00Z",
        duration = 2,
        clubID = club1.clubID
    )

    // 2. Test verisi
    val user2 = UsersDataMem.addUser(name = "Mert", email = "mert@example.com")
    val club2 = ClubsDataMem.addClub(name = "Padel Club B", ownerId = user2.userID)
    val court2 = CourtsDataMem.addCourt(name = "Court B", clubId = club2.clubID)

    val rental2 = RentalsDataMem.addRental(
        userId = user2.userID,
        courtId = court2.courtID,
        startTime = "2025-03-27T15:00:00Z",
        duration = 2,
        clubID = club2.clubID
    )

    // 3. Test verisi
    val user3 = UsersDataMem.addUser(name = "Ali", email = "ali@example.com")
    val club3 = ClubsDataMem.addClub(name = "Padel Club C", ownerId = user3.userID)
    val court3 = CourtsDataMem.addCourt(name = "Court C", clubId = club3.clubID)

    val rental3 = RentalsDataMem.addRental(
        userId = user3.userID,
        courtId = court3.courtID,
        startTime = "2025-03-27T16:00:00Z",
        duration = 2,
        clubID = club3.clubID
    )

    // Verilerin başarıyla eklendiğini doğrulamak için konsola yazdırma
    println("Data inserted successfully:")
    println("User 1: $user1, Club 1: $club1, Court 1: $court1, Rental 1: $rental1")
    println("User 2: $user2, Club 2: $club2, Court 2: $court2, Rental 2: $rental2")
    println("User 3: $user3, Club 3: $club3, Court 3: $court3, Rental 3: $rental3")
}
