package main

import api.clubsWebApi
import api.courtsWebApi
import api.usersWebApi
import api.rentalsWebApi
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.*
import org.http4k.routing.ResourceLoader.Companion.Classpath

// — JSON API handlers under /api —
// (api.clubsWebApi, courtsWebApi, usersWebApi, rentalsWebApi imported)


// — SPA shell + static-content fallback —
fun spaAndStatic(): RoutingHttpHandler {
    val indexHtml = Thread.currentThread().contextClassLoader
        .getResource("static-content/index.html")!!
        .readText()

    return routes(
        "/static-content" bind static(Classpath("static-content")),
        "/"        bind GET to { _: Request -> Response(Status.OK).header("Content-Type", "text/html; charset=UTF-8").body(indexHtml) },
        "/{any:.*}" bind GET to { _: Request -> Response(Status.OK).header("Content-Type", "text/html; charset=UTF-8").body(indexHtml) }
    )
}

fun addTestData() {
    println("✅ Adding test data...")

    val u1 = UsersDataMem.addUser("Yusuf", "yusuf@example.com")
    val u2 = UsersDataMem.addUser("Mert",  "mert@example.com")
    val u3 = UsersDataMem.addUser("Ali",   "ali@example.com")

    val c1 = ClubsDataMem.addClub("Padel Club A", u1.userId)
    val c2 = ClubsDataMem.addClub("Padel Club B", u2.userId)
    val c3 = ClubsDataMem.addClub("Padel Club C", u3.userId)

    listOf(c1 to u1.userId, c2 to u2.userId, c3 to u3.userId).forEach { (club, owner) ->
        val courts = listOf(
            CourtsDataMem.addCourt("Court 1", club.clubID),
            CourtsDataMem.addCourt("Court 2", club.clubID),
            CourtsDataMem.addCourt("Court 3", club.clubID)
        )
        courts.forEachIndexed { idx, court ->
            RentalsDataMem.addRental(clubId = club.clubID, courtId = court.courtID, userId = owner, startTime = "2025-03-27T1${4 + idx}:00:00", duration = 2)
        }
    }

    println("✅ Test data added.")
}

fun main() {
    val PORT = 9000
    addTestData()

    val app = routes(
        clubsWebApi(),
        courtsWebApi(),
        usersWebApi(),
        rentalsWebApi(),
        spaAndStatic()
    )

    app.asServer(SunHttp(PORT)).start().also {
        println("Server running on http://localhost:$PORT")
    }
}