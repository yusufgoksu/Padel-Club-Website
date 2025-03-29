import api.clubsWebApi
import api.courtsWebApi
import api.rentalsWebApi
import api.usersWebApi
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.routes
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

fun main() {
    val app = routes(
        clubsWebApi(), // Kulüp API'si
        courtsWebApi(), // Kort API'si
        rentalsWebApi(), // Kiralama API'si
        usersWebApi() // Kullanıcı API'si
    )

    val server = app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")
    // Önce bir kullanıcı ve bir kulüp ekleyelim
    val user =  UsersDataMem.addUser(name = "yusuf", email = "mert@example.com")
    val club = ClubsDataMem.addClub(name = "Club X", ownerId = user.userID)

    // Kulüp ve kort ekleyelim
    val court = CourtsDataMem.addCourt(name = "Court 1", clubId = club.clubID)

    // Kiralama verisini ekleyelim
    val rental = RentalsDataMem.addRental(
        userId = user.userID,
        courtId = court.courtID,
        startTime = "2025-03-27T14:00:00Z",  // ISO-8601 formatında bir tarih
        duration = 2,
        clubID = club.clubID
    )
    // Kullanıcıyı ekleyelim
    val user1 = UsersDataMem.addUser(name = "Ahmet Yılmaz", email = "ahmet@example.com")

// Kulüp ekleyelim
    val club1 = ClubsDataMem.addClub(name = "Elite Padel Club", ownerId = user.userID)

// Kulüp ve kort ekleyelim
    val court1 = CourtsDataMem.addCourt(name = "Padel Court 1", clubId = club.clubID)




// Kiralama verisini ekleyelim
    val rental1 = RentalsDataMem.addRental(
        userId = user.userID,
        courtId = court.courtID,
        startTime = "2025-04-01T09:30:00Z",  // ISO-8601 formatında bir tarih
        duration = 1,
        clubID = club.clubID
    )






}

