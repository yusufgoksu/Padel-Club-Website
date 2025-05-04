package api

import models.*
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

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

        // 🔥 Kulüpleri HTML formatında listele (Yeni eklenen endpoint)
        "/clubs/html" bind Method.GET to {
            val clubs = ClubServices.getAllClubs()

            val html = buildString {
                append("<!DOCTYPE html><html><head><title>Clubs List</title></head><body>")
                append("<h1>Clubs List</h1>")
                append("<ul>")
                for (club in clubs) {
                    append("<li><a href=\"/clubs/details/${club.clubID}\">${club.name}</a></li>")
                }
                append("</ul>")
                append("<a href=\"/\">Home</a>")
                append("</body></html>")
            }

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        }
    )
}
