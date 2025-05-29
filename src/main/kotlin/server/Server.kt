package main

import api.clubsWebApi
import api.courtsWebApi
import api.usersWebApi
import api.rentalsWebApi
import data.database.ClubsDataDb
import data.database.CourtsDataDb
import data.database.UserDataDb
import data.database.RentalDataDb
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.*
import org.http4k.routing.ResourceLoader.Companion.Classpath



fun addTestDataToDatabase() {
    println("✅ Adding test data to PostgreSQL...")

    val u1Id = UserDataDb.createUser("Yusuf", "yusuf@example.com")
    val u2Id = UserDataDb.createUser("Mert",  "mert@example.com")
    val u3Id = UserDataDb.createUser("Ali",   "ali@example.com")

    val c1Id = ClubsDataDb.createClub("Padel Club A", u1Id)
    val c2Id = ClubsDataDb.createClub("Padel Club B", u2Id)
    val c3Id = ClubsDataDb.createClub("Padel Club C", u3Id)

    listOf(c1Id to u1Id, c2Id to u2Id, c3Id to u3Id).forEach { (clubId, ownerId) ->
        val court1 = CourtsDataDb.createCourt("Court 1", clubId)
        val court2 = CourtsDataDb.createCourt("Court 2", clubId)
        val court3 = CourtsDataDb.createCourt("Court 3", clubId)

        RentalDataDb.createRental(clubId, court1, ownerId, "2025-03-27T14:00:00", 2)
        RentalDataDb.createRental(clubId, court2, ownerId, "2025-03-27T15:00:00", 2)
        RentalDataDb.createRental(clubId, court3, ownerId, "2025-03-27T16:00:00", 2)
    }

    println("✅ Test data added to PostgreSQL.")
}

fun main() {
    val PORT = 9000
    addTestDataToDatabase()

    val app = routes(
        clubsWebApi(),
        courtsWebApi(),
        usersWebApi(),
        rentalsWebApi(),
        spaAndStatic()
    )

    app.asServer(SunHttp(PORT)).start().also {
        println("🚀 Server running on http://localhost:$PORT")
    }
}
