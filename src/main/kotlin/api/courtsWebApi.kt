package api

import models.*
import services.CourtServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun courtsWebApi(): HttpHandler {
    val userLens = Body.auto<User>().toLens()
    val clubLens = Body.auto<Club>().toLens()
    val courtLens = Body.auto<Court>().toLens()
    val rentalLens = Body.auto<Rental>().toLens()

    return routes(
        "/users" bind Method.GET to { Response(Status.OK).body(CourtServices.getUsers().toString()) },
        "/users" bind Method.POST to { request ->
            val user = userLens(request)
            val createdUser = CourtServices.addUser(user.name, user.email)
            Response(Status.CREATED).body(createdUser.toString())
        },

        "/clubs" bind Method.GET to { Response(Status.OK).body(CourtServices.getClubs().toString()) },
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = CourtServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).body(createdClub.toString())
        },

        "/courts" bind Method.GET to { Response(Status.OK).body(CourtServices.getCourts().toString()) },
        "/courts" bind Method.POST to { request ->
            val court = courtLens(request)
            val createdCourt = CourtServices.addCourt(court.name, court.clubId)
            Response(Status.CREATED).body(createdCourt.toString())
        },

        "/rentals" bind Method.GET to { Response(Status.OK).body(CourtServices.getRentals().toString()) },
        "/rentals" bind Method.POST to { request ->
            val rental = rentalLens(request)
            val createdRental = CourtServices.addRental(rental.clubId, rental.courtId, rental.userId, rental.startTime, rental.duration)
            Response(Status.CREATED).body(createdRental.toString())
        }
    )
}
