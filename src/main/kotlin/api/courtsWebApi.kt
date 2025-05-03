package api

import models.*
import services.CourtServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {

    val courtLens = Body.auto<Court>().toLens()
    val courtsLens = Body.auto<List<Court>>().toLens()

    return routes(
        // Tüm kortları listele
        "/courts" bind Method.GET to {
            Response(Status.OK).with(courtsLens of CourtServices.getAllCourts())
        },

        // Yeni kort ekle
        "/courts" bind Method.POST to { request ->
            val court = courtLens(request)
            val createdCourt = CourtServices.addCourt(court.name, court.clubId)
            Response(Status.CREATED).with(courtLens of createdCourt)
        },

        // Kort ID'ye göre kortu getir
        "/courts/{courtID}" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val court = CourtServices.getCourtById(courtID)
                Response(Status.OK).with(courtLens of court!!)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kulüp ID'sine göre kortları listele
        "/courts/club/{clubId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val courtsForClub = CourtServices.getCourtsForClub(clubId)
                Response(Status.OK).with(courtsLens of courtsForClub)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kort ismine göre arama
        "/courts/name/{courtName}" bind Method.GET to { request ->
            val courtName = request.path("courtName") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtByName(courtName)
            return@to if (court != null) {
                Response(Status.OK).with(courtLens of court)
            } else {
                Response(Status.NOT_FOUND)
            }
        }
    )
}
