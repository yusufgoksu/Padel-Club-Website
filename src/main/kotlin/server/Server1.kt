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

fun spaAndStatic(): RoutingHttpHandler {
    val indexHtml = Thread.currentThread().contextClassLoader
        .getResource("static-content/index.html")!!
        .readText()

    return routes(
        "/static-content" bind static(Classpath("static-content")),
        "/" bind GET to { _: Request ->
            Response(Status.OK)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(indexHtml)
        },
        "/{any:.*}" bind GET to { req: Request ->
            if (req.uri.path.startsWith("/api")) {
                Response(Status.NOT_FOUND)
            } else {
                Response(Status.OK)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(indexHtml)
            }
        }
    )
}

fun addTestData() {
    println("✅ Adding test data...")

    val u1 = UsersDataMem.addUser("Yusuf", "yusuf@example.com")
    val u2 = UsersDataMem.addUser("Mert", "mert@example.com")
    val u3 = UsersDataMem.addUser("Ali", "ali@example.com")

    val c1 = ClubsDataMem.addClub("Padel Club A", u1.userId)
    val c2 = ClubsDataMem.addClub("Padel Club B", u2.userId)
    val c3 = ClubsDataMem.addClub("Padel Club C", u3.userId)

    listOf(c1 to u1, c2 to u2, c3 to u3).forEach { (club, user) ->
        val clubId = requireNotNull(club.clubId) { "Club ID must not be null" }

        val courts = listOf(
            CourtsDataMem.addCourt("Court 1", clubId),
            CourtsDataMem.addCourt("Court 2", clubId),
            CourtsDataMem.addCourt("Court 3", clubId)
        )

        courts.forEachIndexed { idx, court ->
            val courtId = requireNotNull(court.courtId) { "Court ID must not be null" }

            RentalsDataMem.addRental(
                clubId = clubId,
                courtId = courtId,
                userId = user.userId,
                startTime = "2025-03-27T1${4 + idx}:00:00",
                duration = 2
            )
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
        println("🚀 Server running on http://localhost:$PORT")
    }
}
