package api

import models.Rental
import services.RentalServices
import kotlinx.serialization.Serializable
import org.http4k.core.*
import org.http4k.core.Method.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.*
import org.http4k.routing.*

fun rentalsWebApi(): RoutingHttpHandler {

    /* ---------- JSON lens’leri ---------- */
    val rentalLens        = Body.auto<Rental>().toLens()
    val rentalsLens       = Body.auto<List<Rental>>().toLens()
    val hoursLens         = Body.auto<List<Int>>().toLens()

    /* ---- PUT için DTO & lens ---------- */
    @Serializable
    data class RentalUpdateReq(val startTime: String, val duration: Int)
    val rentalUpdateLens  = Body.auto<RentalUpdateReq>().toLens()

    /* ---------- Path parçaları ---------- */
    val rentalIdPath = Path.int().of("rentalId")
    val userIdPath   = Path.int().of("userId")
    val clubIdPath   = Path.int().of("clubId")
    val courtIdPath  = Path.int().of("courtId")

    return routes(
        /* ----------------- GET /api/rentals ----------------- */
        "/api/rentals" bind GET to {
            Response(Status.OK).with(rentalsLens of RentalServices.getAllRentals())
        },

        /* ----------------- POST /api/rentals ---------------- */
        "/api/rentals" bind POST to { req ->
            val r = rentalLens(req)
            try {
                val created = RentalServices.addRental(
                    clubId    = r.clubId,
                    courtId   = r.courtId,
                    userId    = r.userId,
                    startTime = r.startTime,
                    duration  = r.duration
                )
                Response(Status.CREATED).with(rentalLens of created)
            } catch (e: Exception) {
                Response(Status.BAD_REQUEST).body("Error: ${e.message}")
            }
        },

        /* ---------------- GET /api/rentals/{id} ------------- */
        "/api/rentals/{rentalId}" bind GET to { req ->
            val id = rentalIdPath(req)
            RentalServices.getRentalById(id)
                ?.let { Response(Status.OK).with(rentalLens of it) }
                ?:   Response(Status.NOT_FOUND).body("Rental not found")
        },

        /* ------------- GET /api/users/{uid}/rentals --------- */
        "/api/users/{userId}/rentals" bind GET to { req ->
            val uid = userIdPath(req)
            Response(Status.OK)
                .with(rentalsLens of RentalServices.getRentalsForUser(uid))
        },

        /* ------- GET /api/clubs/{cid}/courts/{coid}/rentals - */
        "/api/clubs/{clubId}/courts/{courtId}/rentals" bind GET to { req ->
            val cId  = clubIdPath(req)
            val coId = courtIdPath(req)
            val date = Query.string().optional("date")(req)
            val list = RentalServices.getRentalsForCourt(cId, coId, date)
            Response(Status.OK).with(rentalsLens of list)
        },


        /* ---- GET /api/clubs/{cid}/courts/{coid}/available --- */
        "/api/clubs/{clubId}/courts/{courtId}/available" bind GET to { req ->
            val cId  = clubIdPath(req)
            val coId = courtIdPath(req)
            val date = Query.string().required("date")(req)
            val hrs  = RentalServices.getAvailableHours(cId, coId, date)
            Response(Status.OK).with(hoursLens of hrs)
        },

        /* ----------------- PUT /api/rentals/{id} ------------ */
        "/api/rentals/{rentalId}" bind PUT to { req ->
            val id = rentalIdPath(req)
            val updReq = try { rentalUpdateLens(req) }
            catch (e: Exception) {
                return@to Response(Status.BAD_REQUEST).body("Invalid JSON body")
            }

            try {
                val updated = RentalServices.updateRental(
                    id,
                    newStartTime = updReq.startTime,
                    newDuration  = updReq.duration,
                )
                Response(Status.OK).with(rentalLens of updated)

            } catch (e: IllegalStateException) {
                // updateRental kendisi “bulunamadı / başarısız” için IllegalStateException atıyor
                Response(Status.NOT_FOUND).body("Rental not found or update failed")

            } catch (e: Exception) {
                Response(Status.BAD_REQUEST).body("Update failed: ${e.message}")
            }
        },

        /* ---------------- DELETE /api/rentals/{id} ---------- */
        "/api/rentals/{rentalId}" bind DELETE to { req ->
            val id = rentalIdPath(req)
            if (RentalServices.deleteRental(id)) Response(Status.NO_CONTENT)
            else Response(Status.NOT_FOUND)
        },

        /* ------- GET /api/courts/{coid}/users (istatistik) --- */
        "/api/courts/{courtId}/users" bind GET to { req ->
            val coId = courtIdPath(req)
            val stats = RentalServices.usersWithCountsByCourt(coId)
            val json = stats.joinToString(",", "[", "]") {
                """{"userId":${it.first},"rentalCount":${it.second}}"""
            }
            Response(Status.OK)
                .header("Content-Type", "application/json")
                .body(json)
        },

        /* ----- GET /api/users/{uid}/courts (istatistik) ------ */
        "/api/users/{userId}/courts" bind GET to { req ->
            val uid  = userIdPath(req)
            val stats = RentalServices.courtsWithCountsByUser(uid)
            val json = stats.joinToString(",", "[", "]") {
                """{"courtId":${it.first},"rentalCount":${it.second}}"""
            }
            Response(Status.OK)
                .header("Content-Type", "application/json")
                .body(json)
        }
    )
}
