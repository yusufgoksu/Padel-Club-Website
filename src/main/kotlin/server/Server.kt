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

    // Kullanıcılar
    val u1 = UserDataDb.createUser("Yusuf", "yusuf@example.com")
    val u2 = UserDataDb.createUser("Mert",  "mert@example.com")
    val u3 = UserDataDb.createUser("Ali",   "ali@example.com")

    // Kulüpler
    val c1 = ClubsDataDb.createClub("Padel Club A", u1.userId)
    val c2 = ClubsDataDb.createClub("Padel Club B", u2.userId)
    val c3 = ClubsDataDb.createClub("Padel Club C", u3.userId)

    listOf(c1 to u1, c2 to u2, c3 to u3).forEach { (club, owner) ->
        val clubId = club.clubID!!
        val userId = owner.userId

        // Kortlar
        val court1 = CourtsDataDb.createCourt("Court 1", clubId)
        val court2 = CourtsDataDb.createCourt("Court 2", clubId)
        val court3 = CourtsDataDb.createCourt("Court 3", clubId)

        // Kiralamalar (rentalId verilmiyor)
        RentalDataDb.createRental(clubId, court1.courtID!!, userId, "2025-03-27T14:00:00", 2)
        RentalDataDb.createRental(clubId, court2.courtID!!, userId, "2025-03-27T15:00:00", 2)
        RentalDataDb.createRental(clubId, court3.courtID!!, userId, "2025-03-27T16:00:00", 2)
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
