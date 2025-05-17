package server

import api.*
import data.database.UserDataDb
import data.database.ClubsDataDb
import data.database.CourtsDataDb
import data.database.RentalDataDb
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.routes

fun main() {
    println("Inserting test data into database...")

    // 1) Users
    val user1Id = UserDataDb.createUser("Yusuf", "yusuf@example.com")
    val user2Id = UserDataDb.createUser("Mert",  "mert@example.com")
    val user3Id = UserDataDb.createUser("Ali",   "ali@example.com")
    println("Created users: $user1Id, $user2Id, $user3Id")

    // 2) Clubs (each owned by one of the users)
    val club1Id = ClubsDataDb.createClub("Club A", user1Id)
    val club2Id = ClubsDataDb.createClub("Club B", user2Id)
    val club3Id = ClubsDataDb.createClub("Club C", user3Id)
    println("Created clubs: $club1Id, $club2Id, $club3Id")

    // 3) Courts (some courts in each club)
    val courtA1 = CourtsDataDb.createCourt("Court 1", club1Id)
    val courtA2 = CourtsDataDb.createCourt("Court 2", club1Id)
    val courtA3 = CourtsDataDb.createCourt("Court 3", club1Id)
    val courtB1 = CourtsDataDb.createCourt("Court 1", club2Id)
    val courtB2 = CourtsDataDb.createCourt("Court 2", club2Id)
    val courtC1 = CourtsDataDb.createCourt("Court 1", club3Id)
    println("Created courts: $courtA1, $courtA2, $courtA3, $courtB1, $courtB2, $courtC1")

    // 4) Rentals
    val rental1 = RentalDataDb.createRental(clubId  = club1Id, courtId = courtA1, userId = user1Id, date = "2025-03-27T14:00:00", duration = 2)
    val rental2 = RentalDataDb.createRental(clubId  = club1Id, courtId = courtA2, userId = user1Id, date = "2025-03-27T15:00:00", duration = 2)
    val rental3 = RentalDataDb.createRental(clubId  = club1Id, courtId = courtA3, userId = user1Id, date = "2025-03-27T16:00:00", duration = 2)
    val rental4 = RentalDataDb.createRental(clubId  = club2Id, courtId = courtB1, userId = user2Id, date = "2025-03-27T14:00:00", duration = 2)
    val rental5 = RentalDataDb.createRental(clubId  = club2Id, courtId = courtB2, userId = user2Id, date = "2025-03-27T15:00:00", duration = 2)
    val rental6 = RentalDataDb.createRental(clubId  = club3Id, courtId = courtC1, userId = user3Id, date = "2025-03-27T14:00:00", duration = 2)
    println("Created rentals: $rental1, $rental2, $rental3, $rental4, $rental5, $rental6")

    // 5) Start server
    val app = routes(
        usersWebApi(),
        clubsWebApi(),
        courtsWebApi(),
        rentalsWebApi()
    )
    app.asServer(SunHttp(9000)).start().also {
        println("Server running at http://localhost:9000")
    }
}
