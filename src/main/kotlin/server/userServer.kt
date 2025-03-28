package server
import storage.CourtsDataMem
import api.courtsWebApi
import api.usersWebApi
import org.http4k.server.Jetty
import org.http4k.server.asServer
import storage.ClubsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

fun main() {
    val port = 9000
    val app = usersWebApi()

    val server = app.asServer(Jetty(port)).start()
    println("Server running on port $port")
    // Önce bir kullanıcı ve bir kulüp ekleyelim
    val user =  UsersDataMem.addUser(name = "Mert", email = "mert@example.com")
    val club = ClubsDataMem.addClub(name = "Club X", ownerId = user.uid)

    // Kulüp ve kort ekleyelim
    val court = CourtsDataMem.addCourt(name = "Court 1", clubId = club.cid)

    // Kiralama verisini ekleyelim
    val rental = RentalsDataMem.addRental(
        userId = user.uid,
        courtId = court.crid,
        startTime = "2025-03-27T14:00:00Z",  // ISO-8601 formatında bir tarih
        duration = 2  // 2 saatlik kiralama
    )
}

