package api

import models.*
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun clubsWebApi(): HttpHandler {

    val clubLens = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()

    return routes(

        "/clubs" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getClubs())
        },
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = ClubServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).with(clubLens of createdClub)
        },
        "/clubs/{cid}" bind Method.GET to { request ->
            val cid = request.path("cid") ?: return@to Response(Status.BAD_REQUEST)
            val club = ClubServices.getClubById(cid) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(clubLens of club)
        },


    )
}
