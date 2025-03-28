package server
import storage.CourtsDataMem
import api.courtsWebApi
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val port = 9000
    val app = courtsWebApi()
    val server = app.asServer(Jetty(port)).start()
    println("Server running on port $port")
    // Önce bir kullanıcı ve bir kulüp ekleyelim
    val user = CourtsDataMem.addUser(name = "Mert", email = "mert@example.com")
    val club = CourtsDataMem.addClub(name = "Club X", ownerId = user.uid)

    // Kulüp ve kort ekleyelim
    val court = CourtsDataMem.addCourt(name = "Court 1", clubId = club.cid)

    // Kiralama verisini ekleyelim
    val rental = CourtsDataMem.addRental(
        userId = user.uid,
        courtId = court.crid,
        startTime = "2025-03-27T14:00:00Z",  // ISO-8601 formatında bir tarih
        duration = 2  // 2 saatlik kiralama
    )

}

