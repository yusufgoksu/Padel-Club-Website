package api

import models.Club
import services.ClubServices
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.*
import org.http4k.routing.*

fun clubsWebApi(): RoutingHttpHandler {
    val clubLens = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()
    val clubIdPath = Path.int().of("clubId")

    return routes(
        // 📌 Search clubs by name → ÖNCE tanımlanmalı
        "/api/clubs/search" bind GET to { req ->
            val name = req.query("name")

            if (name.isNullOrBlank()) {
                Response(Status.BAD_REQUEST)
                    .body("Missing or empty 'name' parameter")
                    .header("Content-Type", "application/json")
            } else {
                val results = ClubServices.searchClubsByName(name)
                println("🔍 Searching clubs with: $name")
                Response(Status.OK)
                    .with(clubsLens of results)
                    .header("Content-Type", "application/json")
            }
        },

        // 📌 List all clubs
        "/api/clubs" bind GET to {
            val allClubs = ClubServices.getAllClubs()
            Response(Status.OK)
                .with(clubsLens of allClubs)
                .header("Content-Type", "application/json")
        },

        // 📌 Create a new club
        "/api/clubs" bind POST to { req ->
            try {
                val clubReq = clubLens(req)
                val created = ClubServices.addClub(
                    clubId = clubReq.clubID,
                    name = clubReq.name,
                    userID = clubReq.userID
                )
                Response(Status.CREATED)
                    .with(clubLens of created)
                    .header("Content-Type", "application/json")
            } catch (e: IllegalArgumentException) {
                Response(Status.BAD_REQUEST)
                    .body(e.message ?: "Invalid input")
                    .header("Content-Type", "application/json")
            }
        },

        // 📌 Get one club by ID
        "/api/clubs/{clubId}" bind GET to { req ->
            val id = clubIdPath(req)
            val club = ClubServices.getClubById(id)
                ?: return@to Response(Status.NOT_FOUND)
                    .body("Club not found")
                    .header("Content-Type", "application/json")
            Response(Status.OK)
                .with(clubLens of club)
                .header("Content-Type", "application/json")
        }
    )
}
