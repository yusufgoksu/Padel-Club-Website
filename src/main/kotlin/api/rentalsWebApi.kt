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

    val rentalLens  = Body.auto<Rental>().toLens()
    val rentalsLens = Body.auto<List<Rental>>().toLens()

    return routes(
        // Tüm kiralamalar (JSON)
        "/rentals" bind Method.GET to {
            Response(Status.OK).with(rentalsLens of RentalServices.getRentals())
        },

        // Yeni kiralama oluştur (JSON)
        "/rentals" bind Method.POST to { req ->
            val rental  = rentalLens(req)
            val created = RentalServices.addRental(
                rental.clubId, rental.courtId, rental.userId,
                rental.startTime, rental.duration
            )
            Response(Status.CREATED).with(rentalLens of created)
        },

        // Tek bir kiralama detay (HTML)
        "/rentals/{rentalID}" bind Method.GET to { req ->
            val id    = req.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val r     = RentalServices.getRentalById(id) ?: return@to Response(Status.NOT_FOUND)
            val court = CourtServices.getCourtById(r.courtId) ?: return@to Response(Status.NOT_FOUND)
            val club  = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)
            val user  = UserServices.getUserById(r.userId) ?: return@to Response(Status.NOT_FOUND)

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
                    <a href="/users/${user.userID}">View User Details</a><br/>
                    <a href="/rentals/user/${user.userID}">Back to User Rentals</a><br/>
                    <a href="/">Home</a>
                  </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Kullanıcıya ait kiralamalar (HTML)
        "/rentals/user/{userId}" bind Method.GET to { req ->
            val userId = req.path("userId") ?:
            return@to Response(Status.BAD_REQUEST).body("Missing userId")

            val rentals = RentalServices.getRentalsForUser(userId)

            // Tablo satırlarını oluştur, sondaki hücrede "Rental Details" link'i
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
                  <td><a href="/rentals/${r.rentalID}">Rental Details</a></td>
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

        // Kiralama sil (JSON)
        "/rentals/{rentalID}" bind Method.DELETE to { req ->
            val id = req.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            if (RentalServices.deleteRental(id)) Response(Status.NO_CONTENT)
            else Response(Status.NOT_FOUND)
        },

        // Kiralama güncelle (JSON)
        "/rentals/{rentalID}" bind Method.PUT to { req ->
            val id     = req.path("rentalID") ?: return@to Response(Status.BAD_REQUEST)
            val update = rentalLens(req)
            try {
                val updated = RentalServices.updateRental(
                    id, update.startTime, update.duration, update.courtId
                )
                Response(Status.OK).with(rentalLens of updated)
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Diğer JSON-only endpoint’ler…
        "/rentals/available" bind Method.GET to { req ->
            val clubId  = req.query("clubId")  ?: return@to Response(Status.BAD_REQUEST).body("Missing clubId")
            val courtId = req.query("courtId") ?: return@to Response(Status.BAD_REQUEST).body("Missing courtId")
            val date    = req.query("date")    ?: return@to Response(Status.BAD_REQUEST).body("Missing date")
            val hours   = RentalServices.getAvailableHours(clubId, courtId, date)
            Response(Status.OK).body(hours.joinToString(","))
        },

        "/rentals/club/{clubId}/court/{courtId}" bind Method.GET to { req ->
            val clubId  = req.path("clubId")  ?: return@to Response(Status.BAD_REQUEST)
            val courtId = req.path("courtId") ?: return@to Response(Status.BAD_REQUEST)
            val date    = req.query("date")
            val list    = RentalServices.getRentalsForClubAndCourt(clubId, courtId, date)
            Response(Status.OK).with(rentalsLens of list)
        }
    )
}
