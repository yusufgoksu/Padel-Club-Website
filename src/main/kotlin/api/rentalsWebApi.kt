package api

import models.Rental
import services.ClubServices
import services.CourtServices
import services.RentalServices
import services.UserServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun rentalsWebApi(): RoutingHttpHandler {

    // JSON <-> Rental lens
    val rentalLens   = Body.auto<Rental>().toLens()
    val rentalsLens  = Body.auto<List<Rental>>().toLens()

    // Path & query lenses
    val rentalIdPath = Path.int().of("rentalID")
    val userIdPath   = Path.int().of("userId")
    val clubIdPath   = Path.int().of("clubId")
    val courtIdPath  = Path.int().of("courtId")

    val clubIdQuery  = Query.int().required("clubId")
    val courtIdQuery = Query.int().required("courtId")
    val dateQuery    = Query.string().required("date")

    return routes(
        // 1) List all rentals (JSON)
        "/rentals" bind Method.GET to {
            Response(Status.OK)
                .with(rentalsLens of RentalServices.getAllRentals())
        },

        // 2) Create a new rental (JSON)
        "/rentals" bind Method.POST to { req ->
            val r = rentalLens(req)
            val created = RentalServices.addRental(
                r.clubId, r.courtId, r.userId, r.startTime, r.duration
            )
            Response(Status.CREATED)
                .with(rentalLens of created)
        },

        // 3) Get one rental (HTML detail page)
        "/rentals/{rentalID}" bind Method.GET to { req ->
            val id    = rentalIdPath(req)
            val r     = RentalServices.getRentalById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Rental not found")
            val court = CourtServices.getCourtById(r.courtId)
                ?: return@to Response(Status.NOT_FOUND).body("Court not found")
            val club  = ClubServices.getClubById(court.clubId)
                ?: return@to Response(Status.NOT_FOUND).body("Club not found")
            val user  = UserServices.getUserById(r.userId)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")

            val html = """
                <html>
                  <head>
                    <meta charset="UTF-8">
                    <title>Rental Details: ${r.rentalID}</title>
                    <style>
                      body { font-family: Arial, sans-serif; padding: 20px; }
                      table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                      th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                      th { background-color: #f2f2f2; }
                      tr:nth-child(even) { background-color: #f9f9f9; }
                      a { text-decoration: none; color: #007bff; margin-right: 10px; }
                      a:hover { text-decoration: underline; }
                    </style>
                  </head>
                  <body>
                    <h1>Rental Details for ID: ${r.rentalID}</h1>
                    <h2>Court: ${court.name}</h2>
                    <h3>Club: ${club.name}</h3>
                    <table>
                      <tr><th>Rental ID</th><td>${r.rentalID}</td></tr>
                      <tr><th>User ID</th><td>${r.userId}</td></tr>
                      <tr><th>Start Time</th><td>${r.startTime}</td></tr>
                      <tr><th>Duration (hrs)</th><td>${r.duration}</td></tr>
                      <tr><th>Club ID</th><td>${club.clubID}</td></tr>
                      <tr><th>Court ID</th><td>${court.courtID}</td></tr>
                    </table>
                    <br/>
                    <a href="/users/${user.userId}">View User Details</a><br/>
                    <a href="/rentals/user/${user.userId}">Back to User Rentals</a><br/>
                    <a href="/">Home</a>
                  </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // 4) List rentals for a given user (HTML)
        "/rentals/user/{userId}" bind Method.GET to { req ->
            val userId  = userIdPath(req)
            val rentals = RentalServices.getRentalsForUser(userId)

            val rowsHtml = rentals.joinToString("\n") { r ->
                val court = CourtServices.getCourtById(r.courtId)
                val club  = court?.let { ClubServices.getClubById(it.clubId) }
                """
                <tr>
                  <td>${r.rentalID}</td>
                  <td>${club?.name ?: r.clubId}</td>
                  <td>${court?.name ?: r.courtId}</td>
                  <td>${r.startTime}</td>
                  <td>${r.duration}</td>
                  <td><a href="/rentals/${r.rentalID}">Details</a></td>
                </tr>
                """.trimIndent()
            }

            val html = """
                <html>
                  <head>
                    <meta charset="UTF-8">
                    <title>Rentals for User $userId</title>
                    <style>
                      body { font-family: Arial, sans-serif; padding: 20px; }
                      table { border-collapse: collapse; width: 80%; margin-top: 20px; }
                      th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                      th { background-color: #f2f2f2; }
                      tr:nth-child(even) { background-color: #f9f9f9; }
                      a { text-decoration: none; color: #007bff; }
                      a:hover { text-decoration: underline; }
                    </style>
                  </head>
                  <body>
                    <h1>Rentals for User: $userId</h1>
                    <table>
                      <thead>
                        <tr>
                          <th>Rental ID</th>
                          <th>Club</th>
                          <th>Court</th>
                          <th>Start Time</th>
                          <th>Duration (hrs)</th>
                          <th>Details</th>
                        </tr>
                      </thead>
                      <tbody>
                        $rowsHtml
                      </tbody>
                    </table>
                    <br/>
                    <a href="/users/$userId">← Back to User Details</a>
                    <a href="/">Home</a>
                  </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // 5) Delete a rental (JSON)
        "/rentals/{rentalID}" bind Method.DELETE to { req ->
            val id = rentalIdPath(req)
            if (RentalServices.deleteRental(id)) Response(Status.NO_CONTENT)
            else Response(Status.NOT_FOUND)
        },

        // 6) Update a rental (JSON)
        "/rentals/{rentalID}" bind Method.PUT to { req ->
            val id     = rentalIdPath(req)
            val update = rentalLens(req)
            try {
                val updated = RentalServices.updateRental(
                    id, update.startTime, update.duration, update.courtId
                )
                Response(Status.OK)
                    .with(rentalLens of updated)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // 7) Get available hours for a court (JSON)
        "/rentals/available" bind Method.GET to { req ->
            val clubId  = clubIdQuery(req)
            val courtId = courtIdQuery(req)
            val date    = dateQuery(req)
            val hours   = RentalServices.getAvailableHours(clubId, courtId, date)
            Response(Status.OK).body(hours.joinToString(","))
        },

        // 8) List rentals by club & court (JSON)
        "/rentals/club/{clubId}/court/{courtId}" bind Method.GET to { req ->
            val clubId  = clubIdPath(req)
            val courtId = courtIdPath(req)
            val date    = req.query("date")
            val list    = RentalServices.getRentalsForClubAndCourt(clubId, courtId, date)
            Response(Status.OK).with(rentalsLens of list)
        }
    )
}
