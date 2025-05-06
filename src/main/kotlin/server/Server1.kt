package main

import api.*
import pages.clubPages
import pages.courtPages
import pages.homePage
import pages.rentalPages
import pages.userPages
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {


    // 2) HTML sayfa rotaları (root altında)
    val pageRoutes = routes(
        homePage(),
        userPages(),
        clubPages(),
        courtPages(),
        rentalPages()
    )

    // 3) Hepsini birleştir ve sunucuyu başlat
    val app = routes(pageRoutes)
    app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")

    // 4) Sunucu ayağa kalktıktan sonra test verilerini ekle
    val user1 = UsersDataMem.addUser(name = "Yusuf", email = "yusuf@example.com")
    val club1 = ClubsDataMem.addClub(name = "Padel Club A", ownerId = user1.userID)
    val court1A = CourtsDataMem.addCourt(name = "Court A1", clubId = club1.clubID)
    val court1B = CourtsDataMem.addCourt(name = "Court A2", clubId = club1.clubID)
    val court1C = CourtsDataMem.addCourt(name = "Court A3", clubId = club1.clubID)
    RentalsDataMem.addRental(
        userId = user1.userID,
        courtId = court1A.courtID,
        startTime = "2025-03-27T14:00:00Z",
        duration = 2,
        clubID = club1.clubID
    )
    RentalsDataMem.addRental(
        userId = user1.userID,
        courtId = court1B.courtID,
        startTime = "2025-03-27T15:00:00Z",
        duration = 2,
        clubID = club1.clubID
    )
    RentalsDataMem.addRental(
        userId = user1.userID,
        courtId = court1C.courtID,
        startTime = "2025-03-27T16:00:00Z",
        duration = 2,
        clubID = club1.clubID
    )

    val user2 = UsersDataMem.addUser(name = "Mert", email = "mert@example.com")
    val club2 = ClubsDataMem.addClub(name = "Padel Club B", ownerId = user2.userID)
    val court2A = CourtsDataMem.addCourt(name = "Court B1", clubId = club2.clubID)
    val court2B = CourtsDataMem.addCourt(name = "Court B2", clubId = club2.clubID)
    RentalsDataMem.addRental(
        userId = user2.userID,
        courtId = court2A.courtID,
        startTime = "2025-03-27T14:00:00Z",
        duration = 2,
        clubID = club2.clubID
    )
    RentalsDataMem.addRental(
        userId = user2.userID,
        courtId = court2B.courtID,
        startTime = "2025-03-27T15:00:00Z",
        duration = 2,
        clubID = club2.clubID
    )

    val user3 = UsersDataMem.addUser(name = "Ali", email = "ali@example.com")
    val club3 = ClubsDataMem.addClub(name = "Padel Club C", ownerId = user3.userID)
    val court3A = CourtsDataMem.addCourt(name = "Court C1", clubId = club3.clubID)
    RentalsDataMem.addRental(
        userId = user3.userID,
        courtId = court3A.courtID,
        startTime = "2025-03-27T14:00:00Z",
        duration = 2,
        clubID = club3.clubID
    )

    println("Test verisi başarıyla eklendi:")
    println("User 1: $user1, Club 1: $club1, Courts: $court1A, $court1B, $court1C")
    println("User 2: $user2, Club 2: $club2, Courts: $court2A, $court2B")
    println("User 3: $user3, Club 3: $club3, Court: $court3A")
}
