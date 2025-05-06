package pages

import models.Court
import models.Rental
import services.CourtServices
import services.ClubServices
import services.RentalServices
import org.http4k.core.*
import org.http4k.routing.*

fun courtPages(): RoutingHttpHandler = routes(

    // All Courts sayfası
    "/courts" bind Method.GET to { _ ->
        val courts: List<Court> = CourtServices.getAllCourts()
        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset=\"UTF-8\">
                    <title>All Courts</title>
                    <style>
                      body { font-family: Arial, sans-serif; padding: 20px; }
                      table { border-collapse: collapse; width: 80%; margin-top: 20px; }
                      th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                      th { background-color: #f2f2f2; }
                      tr:nth-child(even) { background-color: #f9f9f9; }
                      a { text-decoration: none; color: #007bff; }
                      a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>All Courts</h1>
                    <table>
                        <tr><th>Name</th><th>Club ID</th><th>Details</th></tr>
            """.trimIndent())
            courts.forEach { court ->
                append("<tr><td>${court.name}</td><td>${court.clubId}</td><td><a href=\"/courts/${court.courtID}\">View</a></td></tr>\n")
            }
            append("""
                    </table>
                    <br><a href=\"/\">Back to Home</a>
                </body>
                </html>
            """.trimIndent())
        }
        Response(Status.OK)
            .header("Content-Type","text/html")
            .body(html)
    },

    // Court Details sayfası
    "/courts/{courtID}" bind Method.GET to { req ->
        val courtID = req.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
        val court   = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)
        val club    = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset=\"UTF-8\">
                <title>Court Details</title>
                <style>
                  body { font-family: Arial, sans-serif; padding: 20px; }
                  h1, h2 { color: #333; }
                  a { text-decoration: none; color: #007bff; margin-right: 10px; }
                  a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <h1>Court: ${court.name}</h1>
                <p><strong>Club:</strong> ${club.name}</p>
                <p><strong>Club Owner:</strong> ${club.ownerUid}</p>

                <a href=\"/courts/${courtID}/rentals\">View Rentals</a><br>
                <a href=\"/courts/club/${club.clubID}\">Back to Courts in Club</a><br>
                <a href=\"/\">Back to Home</a>
            </body>
            </html>
        """.trimIndent()

        Response(Status.OK)
            .header("Content-Type","text/html")
            .body(html)
    },

    // Courts in Club sayfası
    "/courts/club/{clubId}" bind Method.GET to { req ->
        val clubId = req.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
        val courts = CourtServices.getCourtsForClub(clubId)
        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head><meta charset=\"UTF-8\"><title>Courts in Club</title></head>
                <body>
                    <h1>Courts in Club</h1>
                    <ul>
            """.trimIndent())
            courts.forEach { c ->
                append("<li><a href=\"/courts/${c.courtID}\">${c.name}</a></li>\n")
            }
            append("""
                    </ul>
                    <a href=\"/clubs\">Back to Clubs</a>
                    <a href=\"/\">Back to Home</a>
                </body>
                </html>
            """.trimIndent())
        }
        Response(Status.OK)
            .header("Content-Type","text/html")
            .body(html)
    },

    // Court Rentals List sayfası
    "/courts/{courtID}/rentals" bind Method.GET to { req ->
        val courtID = req.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
        val court   = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)
        val clubId  = court.clubId
        val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtID)

        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset=\"UTF-8\">
                    <title>Rentals for Court: ${court.name}</title>
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
                    <h1>Rentals for Court: ${court.name}</h1>
                    <table>
                        <thead>
                            <tr>
                                <th>Start Time</th>
                                <th>Duration (hrs)</th>
                                <th>User ID</th>
                                <th>Details</th>
                            </tr>
                        </thead>
                        <tbody>
            """.trimIndent())
            rentals.forEach { r ->
                append("""
                    <tr>
                        <td>${r.startTime}</td>
                        <td>${r.duration}</td>
                        <td>${r.userId}</td>
                        <td><a href=\"/rentals/${r.rentalID}\">Rental Details</a></td>
                    </tr>
                """)
            }
            append("""
                        </tbody>
                    </table>
                    <br>
                    <a href=\"/courts/${courtID}\">Back to Court</a>
                    <a href=\"/\">Back to Home</a>
                </body>
                </html>
            """.trimIndent())
        }

        Response(Status.OK)
            .header("Content-Type","text/html")
            .body(html)
    }
)
