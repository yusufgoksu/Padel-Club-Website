package api

import models.Club
import services.ClubServices
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.*
import org.http4k.routing.*

fun clubsWebApi(): RoutingHttpHandler {
    // JSON ↔ Club lenses
    val clubLens   = Body.auto<Club>().toLens()
    val clubsLens  = Body.auto<List<Club>>().toLens()
    val clubIdPath = Path.int().of("clubId")

    return routes(
        // 1) List all clubs
        "/api/clubs" bind GET to {
            Response(Status.OK)
                .with(clubsLens of ClubServices.getAllClubs())
        },

        // 2) Create a new club with manual clubId
        "/api/clubs" bind POST to { req ->
            try {
                val clubReq = clubLens(req)  // clubReq has clubID, name, userID
                val created = ClubServices.addClub(
                    clubId = clubReq.clubID,
                    name = clubReq.name,
                    userID = clubReq.userID
                )
                Response(Status.CREATED)
                    .with(clubLens of created)
            } catch (e: IllegalArgumentException) {
                Response(Status.BAD_REQUEST)
                    .body(e.message ?: "Invalid input")
            }
        },

        // 3) Get one club by ID
        "/api/clubs/{clubId}" bind GET to { req ->
            val id = clubIdPath(req)
            val club = ClubServices.getClubById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Club not found")
            Response(Status.OK)
                .with(clubLens of club)
        }
    )
}
