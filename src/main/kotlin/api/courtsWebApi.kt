package api

import models.*
import services.CourtServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun courtsWebApi(): HttpHandler {
    val userLens = Body.auto<User>().toLens()
    val usersLens = Body.auto<List<User>>().toLens()

    val clubLens = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()

    val courtLens = Body.auto<Court>().toLens()
    val courtsLens = Body.auto<List<Court>>().toLens()

    val rentalLens = Body.auto<Rental>().toLens()
    val rentalsLens = Body.auto<List<Rental>>().toLens()

    return routes(
        "/users" bind Method.GET to {
            Response(Status.OK).with(usersLens of CourtServices.getUsers())
        },
        "/users" bind Method.POST to { request ->
            val user = userLens(request)
            val createdUser = CourtServices.addUser(user.name, user.email)
            Response(Status.CREATED).with(userLens of createdUser)
        },

        "/clubs" bind Method.GET to {
            Response(Status.OK).with(clubsLens of CourtServices.getClubs())
        },
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = CourtServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).with(clubLens of createdClub)
        },

        "/courts" bind Method.GET to {
            Response(Status.OK).with(courtsLens of CourtServices.getCourts())
        },
        "/courts" bind Method.POST to { request ->
            val court = courtLens(request)
            val createdCourt = CourtServices.addCourt(court.name, court.clubId)
            Response(Status.CREATED).with(courtLens of createdCourt)
        },

        "/rentals" bind Method.GET to {
            Response(Status.OK).with(rentalsLens of CourtServices.getRentals())
        },
        "/rentals" bind Method.POST to { request ->
            val rental = rentalLens(request)
            val createdRental = CourtServices.addRental(rental.clubId, rental.courtId, rental.userId, rental.startTime, rental.duration)
            Response(Status.CREATED).with(rentalLens of createdRental)
        }
    )
}
