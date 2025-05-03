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
        // GET /rentals -> Tüm kiralamaları getir
        "/rentals" bind Method.GET to {
            Response(Status.OK).with(rentalsLens of RentalServices.getRentals())
        },

        // POST /rentals -> Yeni bir kiralama ekle
        "/rentals" bind Method.POST to { request ->
            val rental = rentalLens(request)
            val createdRental = RentalServices.addRental(
                rental.clubId, rental.courtId, rental.userId, rental.startTime, rental.duration
            )
            Response(Status.CREATED).with(rentalLens of createdRental)
        },

        // GET /rentals/{rentalID} -> ID'ye göre kiralama getir
        "/rentals/{rentalID}" bind Method.GET to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val rental = RentalServices.getRentalById(rentalID)
                ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(rentalLens of rental)
        },

        // GET /rentals/available -> Mevcut saatleri getir
        "/rentals/available" bind Method.GET to { request ->
            val clubId = request.query("clubId") ?: return@to Response(Status.BAD_REQUEST).body("Missing clubId")
            val courtId = request.query("courtId") ?: return@to Response(Status.BAD_REQUEST).body("Missing courtId")
            val date = request.query("date") ?: return@to Response(Status.BAD_REQUEST).body("Missing date")

            val availableHours = RentalServices.getAvailableHours(clubId, courtId, date)
            Response(Status.OK).body(availableHours.joinToString(","))
        },

        // GET /rentals/club/{clubId}/court/{courtId} -> Belirli bir kulüp ve kort için kiralamaları getir
        "/rentals/club/{clubId}/court/{courtId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            val courtId = request.path("courtId") ?: return@to Response(Status.BAD_REQUEST)
            val date = request.query("date")

            val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtId, date)
            Response(Status.OK).with(rentalsLens of rentals)
        },

        // GET /rentals/user/{userId} -> Kullanıcıya ait kiralamaları getir
        "/rentals/user/{userId}" bind Method.GET to { request ->
            val userId = request.path("userId") ?: return@to Response(Status.BAD_REQUEST)
            val rentals = RentalServices.getRentalsForUser(userId)
            Response(Status.OK).with(rentalsLens of rentals)
        },

        // DELETE /rentals/{rentalID} -> Kiralamayı sil
        "/rentals/{rentalID}" bind Method.DELETE to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val isDeleted = RentalServices.deleteRental(rentalID)
            if (isDeleted) {
                Response(Status.NO_CONTENT)
            } else {
                Response(Status.NOT_FOUND)
            }
        },

        // PUT /rentals/{rentalID} -> Kiralamayı güncelle
        "/rentals/{rentalID}" bind Method.PUT to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val rental = rentalLens(request)
            try {
                val updatedRental = RentalServices.updateRental(
                    rentalID,
                    rental.startTime,
                    rental.duration,
                    rental.courtId
                )
                Response(Status.OK).with(rentalLens of updatedRental)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        }
    )
}
