package api

import models.*
import services.RentalServices
import services.CourtServices
import services.ClubServices
import services.UserServices
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
                rental.clubId, rental.courtId, rental.userId, rental.startTime, rental.duration
            )
            Response(Status.CREATED).with(rentalLens of createdRental)
        },

        "/rentals/{rentalID}" bind Method.GET to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val rental = RentalServices.getRentalById(rentalID)
                ?: return@to Response(Status.NOT_FOUND)

            val court = CourtServices.getCourtById(rental.courtId) ?: return@to Response(Status.NOT_FOUND)
            val club = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)
            val user = UserServices.getUserById(rental.userId) ?: return@to Response(Status.NOT_FOUND)

            val html = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Rental Details for Rental ID: ${rental.rentalID}</title>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        h1 { color: #333; }
                        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                        th { background-color: #f2f2f2; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        a {
                            text-decoration: none;
                            color: #007bff;
                            margin-right: 10px;
                        }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Rental Details for Rental ID: ${rental.rentalID}</h1>
                    <h2>Court: ${court.name}</h2>
                    <h3>Club: ${club.name}</h3>
                    
                    <table>
                        <tr><th>Rental ID</th><td>${rental.rentalID}</td></tr>
                        <tr><th>User ID</th><td>${rental.userId}</td></tr>
                        <tr><th>Start Time</th><td>${rental.startTime}</td></tr>
                        <tr><th>Duration (hours)</th><td>${rental.duration}</td></tr>
                        <tr><th>Club ID</th><td>${club.clubID}</td></tr>
                        <tr><th>Court ID</th><td>${court.courtID}</td></tr>
                    </table>

                    <br>
                    <!-- View User Details butonu eklendi -->
                    <a href="/users/${user.userID}" class="btn btn-primary">View User Details</a><br>

                    <a href="/courts/${court.courtID}/rentals">Back to Rentals List</a><br>
                    <a href="/courts/${court.courtID}">Back to Court Details</a><br>
                    <a href="/">Back to Home</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        "/rentals/available" bind Method.GET to { request ->
            val clubId = request.query("clubId") ?: return@to Response(Status.BAD_REQUEST).body("Missing clubId")
            val courtId = request.query("courtId") ?: return@to Response(Status.BAD_REQUEST).body("Missing courtId")
            val date = request.query("date") ?: return@to Response(Status.BAD_REQUEST).body("Missing date")

            val availableHours = RentalServices.getAvailableHours(clubId, courtId, date)
            Response(Status.OK).body(availableHours.joinToString(","))
        },

        "/rentals/club/{clubId}/court/{courtId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            val courtId = request.path("courtId") ?: return@to Response(Status.BAD_REQUEST)
            val date = request.query("date")

            val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtId, date)
            Response(Status.OK).with(rentalsLens of rentals)
        },

        "/rentals/user/{userId}" bind Method.GET to { request ->
            val userId = request.path("userId") ?: return@to Response(Status.BAD_REQUEST)
            val rentals = RentalServices.getRentalsForUser(userId)
            Response(Status.OK).with(rentalsLens of rentals)
        },

        "/rentals/{rentalID}" bind Method.DELETE to { request ->
            val rentalID = request.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val isDeleted = RentalServices.deleteRental(rentalID)
            if (isDeleted) {
                Response(Status.NO_CONTENT)
            } else {
                Response(Status.NOT_FOUND)
            }
        },

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
