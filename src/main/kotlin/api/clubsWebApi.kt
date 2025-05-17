package api

import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.lens.Path
import org.http4k.lens.int
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.routing.ResourceLoader.Companion.Classpath
import services.ClubServices
import models.Club

// — JSON API handlers under /api —
fun clubsWebApi(): RoutingHttpHandler {
    val clubLens   = Body.auto<Club>().toLens()
    val clubsLens  = Body.auto<List<Club>>().toLens()
    val clubIdPath = Path.int().of("clubId")

    return routes(
        "/api/clubs" bind GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getAllClubs())
        },
        "/api/clubs" bind POST to { req ->
            val clubReq = clubLens(req)
            val created = ClubServices.addClub(
                name   = clubReq.name,
                userID = clubReq.userID
            )
            Response(Status.CREATED).with(clubLens of created)
        },
        "/api/clubs/{clubId}" bind GET to { req ->
            val id = clubIdPath(req)
            val club = ClubServices.getClubById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Club not found")
            Response(Status.OK).with(clubLens of club)
        }
        // … add courtsWebApi(), usersWebApi(), rentalsWebApi() here …
    )
}

// — SPA shell + static-content fallback —
fun spaAndStatic(): RoutingHttpHandler {
    // Load the same index.html for ALL non-API GETs
    val indexHtml = Thread.currentThread().contextClassLoader
        .getResource("static-content/index.html")!!
        .readText()

    return routes(
        // 1) Serve your client-side files
        "/static-content" bind static(Classpath("static-content")),

        // 2) Root → index.html
        "/" bind GET to { _: Request ->
            Response(Status.OK)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(indexHtml)
        },

        // 3) Catch-all → index.html (e.g. /clubs, /clubs/42, /users/5/rentals/123, etc.)
        "/{any:.*}" bind GET to { _: Request ->
            Response(Status.OK)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(indexHtml)
        }
    )
}

