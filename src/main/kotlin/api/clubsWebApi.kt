package api

import models.*
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*
import java.io.File

fun clubsWebApi(): RoutingHttpHandler {

    val clubLens = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()

    return routes(
        // Tüm kulüpleri listele (JSON)
        "/clubs" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getClubs())
        },

        // Yeni bir kulüp oluştur
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = ClubServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).with(clubLens of createdClub)
        },

        // Kulübü ID'sine göre getirme (JSON)
        "/clubs/{clubID}" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val club = ClubServices.getClubById(clubID) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(clubLens of club)
        },

        // Kulüp detayları (JSON)
        "/clubs/details/{clubID}" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val clubDetails = ClubServices.getClubDetails(clubID)
            if (clubDetails != null) {
                Response(Status.OK).with(clubLens of clubDetails)
            } else {
                Response(Status.NOT_FOUND)
            }
        },

        // Tüm kulüpleri listele (JSON)
        "/clubs/all" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getAllClubs())
        },

        return routes(
            // Tüm kulüpleri listele (HTML formatında)
            "/clubs" bind Method.GET to {
                val clubs = ClubServices.getClubs()

                val clubsHtml = buildString {
                    append("<!DOCTYPE html><html><head><title>Clubs List</title></head><body>")
                    append("<h1>Clubs List</h1>")
                    append("<ul>")
                    for (club in clubs) {
                        append("<li><a href=\"/clubs/details/${club.clubID}\">${club.name}</a></li>")
                    }
                    append("</ul>")
                    append("<a href=\"/\">Back to Home</a>")
                    append("</body></html>")
                }

                Response(Status.OK).body(clubsHtml).header("Content-Type", "text/html")
            },

            // Kulüp detayları (JSON formatında)
            "/clubs/details/{clubID}" bind Method.GET to { request ->
                val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
                val club = ClubServices.getClubById(clubID) ?: return@to Response(Status.NOT_FOUND)

                val clubDetailsHtml = File("resources/views/club_details.html").readText()
                    .replace("{{clubName}}", club.name)
                    .replace("{{clubOwner}}", club.ownerUid)

                Response(Status.OK).body(clubDetailsHtml).header("Content-Type", "text/html")
            }
        )
    )
}
