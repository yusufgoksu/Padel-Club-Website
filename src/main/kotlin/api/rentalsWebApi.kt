package api

import models.Rental
import services.RentalServices
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun rentalsWebApi(): RoutingHttpHandler {
    // JSON ↔ Rental lenses
    val rentalLens    = Body.auto<Rental>().toLens()
    val rentalsLens   = Body.auto<List<Rental>>().toLens()

    // Path & query lenses (lower-camel)
    val rentalIdPath  = Path.int().of("rentalId")
    val userIdPath    = Path.int().of("userId")
    val clubIdPath    = Path.int().of("clubId")
    val courtIdPath   = Path.int().of("courtId")

    return routes(
        // 1) List all rentals
        "/api/rentals" bind GET to {
            Response(Status.OK).with(rentalsLens of RentalServices.getAllRentals())
        },

        // 2) Create a new rental
        "/api/rentals" bind POST to { req ->
            val rentalReq = rentalLens(req)
            val created = RentalServices.addRental(
                rentalReq.clubId,
                rentalReq.courtId,
                rentalReq.userId,
                rentalReq.startTime,
                rentalReq.duration
            )
            Response(Status.CREATED).with(rentalLens of created)
        },

        // 3) Get one rental
        "/api/rentals/{rentalId}" bind GET to { req ->
            val id = rentalIdPath(req)
            val rental = RentalServices.getRentalById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Rental not found")
            Response(Status.OK).with(rentalLens of rental)
        },

        // 4) List rentals for a given user
        "/api/users/{userId}/rentals" bind GET to { req ->
            val uId = userIdPath(req)
            Response(Status.OK).with(rentalsLens of RentalServices.getRentalsForUser(uId))
        },

        // 5) Delete a rental
        "/api/rentals/{rentalId}" bind DELETE to { req ->
            val id = rentalIdPath(req)
            if (RentalServices.deleteRental(id)) Response(Status.NO_CONTENT)
            else Response(Status.NOT_FOUND)
        },

        // 6) Update a rental
        "/api/rentals/{rentalId}" bind PUT to { req ->
            val id = rentalIdPath(req)
            val upd = rentalLens(req)
            try {
                val updated = RentalServices.updateRental(
                    id,
                    upd.startTime,
                    upd.duration,
                    upd.courtId
                )
                Response(Status.OK).with(rentalLens of updated)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // 7) Get available hours for a court on a given date
        "/api/rentals/available" bind GET to { req ->
            val cId     = Query.int().required("clubId")(req)
            val coId    = Query.int().required("courtId")(req)
            val dateStr = Query.string().required("date")(req)
            val hours   = RentalServices.getAvailableHours(cId, coId, dateStr)
            Response(Status.OK).body(hours.joinToString(","))
        },

        // 8) List rentals by club & court
        "/api/clubs/{clubId}/courts/{courtId}/rentals" bind GET to { req ->
            val cId  = clubIdPath(req)
            val coId = courtIdPath(req)
            Response(Status.OK).with(
                rentalsLens of RentalServices.getRentalsForClubAndCourt(cId, coId)
            )
        }
    )
}
