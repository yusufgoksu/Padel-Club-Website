package pages

import models.Rental
import services.RentalServices
import services.CourtServices
import services.ClubServices
import services.UserServices
import org.http4k.core.*
import org.http4k.routing.*

fun rentalPages(): RoutingHttpHandler = routes(

    // Rental Details sayfası (HTML)
    "/rentals/{rentalID}" bind Method.GET to { req ->
        val rentalID = req.path("rentalID")
            ?: return@to Response(Status.BAD_REQUEST).body("Missing rentalID")
        val r = RentalServices.getRentalById(rentalID)
            ?: return@to Response(Status.NOT_FOUND).body("Rental not found")
        val court = CourtServices.getCourtById(r.courtId)
            ?: return@to Response(Status.NOT_FOUND).body("Court not found")
        val club = ClubServices.getClubById(court.clubId)
            ?: return@to Response(Status.NOT_FOUND).body("Club not found")
        val user = UserServices.getUserById(r.userId)
            ?: return@to Response(Status.NOT_FOUND).body("User not found")

        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset=\"UTF-8\">
                    <title>Rental Details</title>
                    <style>
                      body { font-family: Arial, sans-serif; padding: 20px; }
                      table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                      th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                      th { background-color: #f2f2f2; }
                      tr:nth-child(even) { background-color: #f9f9f9; }
                      a { text-decoration: none; color: #007bff; }
                      a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Rental Details for ID: ${r.rentalID}</h1>
                    <table>
                        <tr><th>Rental ID</th><td>${r.rentalID}</td></tr>
                        <tr><th>User ID</th><td>${r.userId}</td></tr>
                        <tr><th>Court</th><td>${court.name}</td></tr>
                        <tr><th>Club</th><td>${club.name}</td></tr>
                        <tr><th>Start Time</th><td>${r.startTime}</td></tr>
                        <tr><th>Duration (hrs)</th><td>${r.duration}</td></tr>
                    </table>
                    <br/>
                    <a href=\"/users/${user.userID}\">View User Details</a><br/>
                    <a href=\"/rentals/user/${user.userID}\">Back to User Rentals</a><br/>
                    <a href=\"/\">Home</a>
                </body>
                </html>
            """.trimIndent())
        }

        Response(Status.OK)
            .header("Content-Type", "text/html")
            .body(html)
    },

    // Kullanıcıya ait kiralamalar sayfası (HTML)
    "/rentals/user/{userId}" bind Method.GET to { req ->
        val userId = req.path("userId")
            ?: return@to Response(Status.BAD_REQUEST).body("Missing userId")
        val rentals = RentalServices.getRentalsForUser(userId)

        val rows = rentals.joinToString("\n") { r ->
            val court = CourtServices.getCourtById(r.courtId)
            val club  = court?.let { ClubServices.getClubById(it.clubId) }
            """
            <tr>
              <td>${r.rentalID}</td>
              <td>${club?.name ?: r.clubId}</td>
              <td>${court?.name ?: r.courtId}</td>
              <td>${r.startTime}</td>
              <td>${r.duration}</td>
              <td><a href=\"/rentals/${r.rentalID}\">Rental Details</a></td>
            </tr>
            """.trimIndent()
        }

        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset=\"UTF-8\">
                    <title>Rentals for User</title>
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
                          $rows
                        </tbody>
                    </table>
                    <br/>
                    <a href=\"/users/$userId\">← Back to User Details</a>
                    <a href=\"/\">Home</a>
                </body>
                </html>
            """.trimIndent())
        }

        Response(Status.OK)
            .header("Content-Type", "text/html")
            .body(html)
    }
)
