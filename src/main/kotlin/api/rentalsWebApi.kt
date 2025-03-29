package api

import models.*
import services.RentalServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun rentalsWebApi(): RoutingHttpHandler {

    val rentalLens = Body.auto<Rental>().toLens()
    val rentalsLens = Body.auto<List<Rental>>().toLens()

    return routes(
        "/rentals" bind Method.GET to {
            Response(Status.OK).with(rentalsLens of RentalServices.getRentals())
        },
        "/rentals" bind Method.POST to { request ->
            val rental = rentalLens(request)
            val createdRental = RentalServices.addRental(rental.clubId, rental.courtId, rental.userId, rental.startTime, rental.duration)
            Response(Status.CREATED).with(rentalLens of createdRental)
        },
        "/rentals/{rid}" bind Method.GET to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val rental = RentalServices.getRentalById(rentalID) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(rentalLens of rental)
        }
    )
}
