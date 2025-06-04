package api

import models.Court
import services.CourtServices
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.int
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun courtsWebApi(): RoutingHttpHandler {

    val courtLens    = Body.auto<Court>().toLens()
    val courtsLens   = Body.auto<List<Court>>().toLens()
    val courtIdPath  = Path.int().of("courtId")
    val clubIdPath   = Path.int().of("clubId")

    return routes(
        // 1) Tüm kortları JSON formatında listele
        "/api/courts" bind GET to {
            val allCourts = CourtServices.getAllCourts()
            Response(Status.OK).with(courtsLens of allCourts)
        },

        // 2) Yeni bir kort ekle (manuel courtID bekleniyorsa)
        "/api/courts" bind Method.POST to { req ->
            try {
                val courtReq = courtLens(req)  // courtReq.courtID, courtReq.name, courtReq.clubId olmalı
                val created = CourtServices.addCourt(
                    courtId = courtReq.courtID,  // manuel ID ise
                    name = courtReq.name,
                    clubId = courtReq.clubId
                )
                Response(Status.CREATED).with(courtLens of created)
            } catch (e: IllegalArgumentException) {
                Response(Status.BAD_REQUEST).body(e.message ?: "Invalid input")
            }
        },

        // 3) Tek bir kortu JSON olarak getir
        "/api/courts/{courtId}" bind GET to { req ->
            val id = courtIdPath(req)
            val court = CourtServices.getCourtById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Court not found")
            Response(Status.OK).with(courtLens of court)
        },

        // 4) Belirli bir kulübe ait kortları JSON formatında getir
        "/api/clubs/{clubId}/courts" bind GET to { req ->
            val cId = clubIdPath(req)
            val courts = CourtServices.getCourtsForClub(cId)
            Response(Status.OK).with(courtsLens of courts)
        }
    )
}
