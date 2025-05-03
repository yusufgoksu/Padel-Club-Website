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
            val createdRental = RentalServices.addRental(
                clubId = rental.clubId,
                courtId = rental.courtId,
                userId = rental.userId,
                startTime = rental.startTime,
                duration = rental.duration
            )
            Response(Status.CREATED).with(rentalLens of createdRental)
        },
        "/rentals/{rid}" bind Method.GET to { request ->
            val rentalID = request.path("rid") ?: return@to Response(Status.BAD_REQUEST)
            val rental = RentalServices.getRentalById(rentalID)
                ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(rentalLens of rental)
        },
        "/rentals/{rid}" bind Method.DELETE to { request ->
            val rentalID = request.path("rid") ?: return@to Response(Status.BAD_REQUEST)
            val deleted = RentalServices.deleteRental(rentalID)
            if (deleted) Response(Status.NO_CONTENT)
            else Response(Status.NOT_FOUND).body("Kiralama bulunamadı")
        },

        // 👇 BURADA EKSİK OLAN VİRGÜLÜ EKLEDİK
        "/rentals/{rid}" bind Method.PUT to { request ->
            val rentalID = request.path("rid") ?: return@to Response(Status.BAD_REQUEST)
            val rentalUpdate = rentalLens(request)

            try {
                val updatedRental = RentalServices.updateRental(
                    rentalID = rentalID,
                    newStartTime = rentalUpdate.startTime,
                    newDuration = rentalUpdate.duration,
                    newCourtId = rentalUpdate.courtId
                )
                Response(Status.OK).with(rentalLens of updatedRental)
            } catch (e: Exception) {
                Response(Status.NOT_FOUND).body(e.message ?: "Rental update failed")
            }
        }
    )
}
