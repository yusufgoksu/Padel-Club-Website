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
        // Tüm kulüpleri listele
        "/clubs" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getClubs())
        },
        // Yeni bir kulüp oluştur
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = ClubServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).with(clubLens of createdClub)
        },
        // Kulübü ID'sine göre getirme
        "/clubs/{clubID}" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val club = ClubServices.getClubById(clubID) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(clubLens of club)
        },
        // Tüm kulüp detaylarını listele
        "/clubs/details/{clubID}" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val clubDetails = ClubServices.getClubDetails(clubID)
            if (clubDetails != null) {
                Response(Status.OK).with(clubLens of clubDetails)
            } else {
                Response(Status.NOT_FOUND)
            }
        },
        // Tüm kulüpleri listeleme (Ekstra bir istek olarak)
        "/clubs/all" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getAllClubs())
        }
    )
}
