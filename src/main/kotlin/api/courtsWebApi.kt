package api

import models.*
import services.CourtServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun courtsWebApi(): HttpHandler {

    val courtLens = Body.auto<Court>().toLens()
    val courtsLens = Body.auto<List<Court>>().toLens()

    return routes(

        "/courts" bind Method.GET to {
            Response(Status.OK).with(courtsLens of CourtServices.getCourts())
        },
        "/courts" bind Method.POST to { request ->
            val court = courtLens(request)
            val createdCourt = CourtServices.addCourt(court.name, court.clubId)
            Response(Status.CREATED).with(courtLens of createdCourt)
        },
        "/courts/{crid}" bind Method.GET to { request ->
            val crid = request.path("crid") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtById(crid) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(courtLens of court)
        }


    )
}