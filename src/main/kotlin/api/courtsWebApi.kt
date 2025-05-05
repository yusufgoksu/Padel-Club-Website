package api

import models.*
import services.CourtServices
import services.RentalServices
import services.ClubServices
import org.http4k.core.*
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {
    return routes(
        "/courts" bind Method.GET to {
            val courts = CourtServices.getAllCourts()
            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html>
                    <head><meta charset="UTF-8"><title>All Courts</title>
                    <style>
                        body { font-family: Arial; padding: 20px; }
                        ul { list-style-type: none; padding: 0; }
                        li { margin: 8px 0; }
                    </style>
                    </head>
                    <body>
                    <h1>All Courts</h1>
                    <ul>
                """.trimIndent())

                for (court in courts) {
                    append("<li><a href='/courts/${court.courtID}'>${court.name}</a></li>")
                }

                append("""
                    </ul>
                    <a href='/'>Back to Home</a>
                    </body></html>
                """.trimIndent())
            }
            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        "/courts" bind Method.POST to { request ->
            val court = request.bodyString()
            Response(Status.CREATED).body("Court Created: $court").header("Content-Type", "text/html")
        },

        "/courts/{courtID}" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val court = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)
                val club = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)
                val rentals = RentalServices.getRentalsForClubAndCourt(club.clubID, court.courtID)

                val rentalRows = rentals.joinToString("\n") {
                    "<tr><td>${it.startTime}</td><td>${it.duration} hour(s)</td></tr>"
                }

                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>${court.name}</title>
                        <style>
                            body { font-family: Arial; padding: 20px; }
                            table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                            th, td { border: 1px solid #ccc; padding: 8px; }
                            th { background: #eee; }
                            .buttons a {
                                display: inline-block; margin: 10px 10px 0 0;
                                background: #007bff; color: white; padding: 8px 12px;
                                text-decoration: none; border-radius: 4px;
                            }
                        </style>
                    </head>
                    <body>
                        <h1>${court.name}</h1>
                        <p><strong>Club:</strong> ${club.name}</p>
                        <p><strong>Owner UID:</strong> ${club.ownerUid}</p>
                        <h3>Booked Rentals</h3>
                        <table>
                            <thead><tr><th>Start</th><th>Duration</th></tr></thead>
                            <tbody>$rentalRows</tbody>
                        </table>
                        <div class="buttons">
                            <a href="/rentals/court/${court.courtID}">View Rental List</a>
                            <a href="/courts/club/${club.clubID}">Back to Courts in Club</a>
                            <a href="/clubs">Back to Clubs</a>
                            <a href="/">Home</a>
                        </div>
                    </body>
                    </html>
                """.trimIndent()

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        "/courts/club/{clubId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val courts = CourtServices.getCourtsForClub(clubId)
                val html = buildString {
                    append("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <title>Courts for Club</title>
                            <style>
                                body { font-family: Arial; padding: 20px; }
                                ul { list-style-type: none; padding: 0; }
                                li { margin: 8px 0; }
                            </style>
                        </head>
                        <body>
                            <h1>Courts for Club</h1>
                            <ul>
                    """.trimIndent())

                    for (court in courts) {
                        append("<li><a href='/courts/${court.courtID}'>${court.name}</a></li>")
                    }

                    append("""
                            </ul>
                            <a href="/clubs">Back to Clubs List</a><br>
                            <a href="/">Back to Home</a>
                        </body>
                        </html>
                    """.trimIndent())
                }

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        "/courts/name/{courtName}" bind Method.GET to { request ->
            val courtName = request.path("courtName") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtByName(courtName)
            return@to if (court != null) {
                Response(Status.OK).body("<h1>Court: ${court.name}</h1>").header("Content-Type", "text/html")
            } else {
                Response(Status.NOT_FOUND)
            }
        },

        "/rentals/court/{courtId}" bind Method.GET to { request ->
            val courtId = request.path("courtId") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val court = CourtServices.getCourtById(courtId) ?: return@to Response(Status.NOT_FOUND)
                val club = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)
                val rentals = RentalServices.getRentalsForClubAndCourt(club.clubID, court.courtID)

                val rows = rentals.joinToString("\n") {
                    "<tr><td>${it.startTime}</td><td>${it.duration}</td><td>${it.userId}</td></tr>"
                }

                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Rentals for ${court.name}</title>
                        <style>
                            body { font-family: Arial; padding: 20px; }
                            table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                            th, td { border: 1px solid #ccc; padding: 8px; }
                            th { background: #eee; }
                            .buttons a {
                                display: inline-block; margin: 10px 10px 0 0;
                                background: #007bff; color: white; padding: 8px 12px;
                                text-decoration: none; border-radius: 4px;
                            }
                        </style>
                    </head>
                    <body>
                        <h1>Rentals for ${court.name}</h1>
                        <table>
                            <thead><tr><th>Start Time</th><th>Duration</th><th>User</th></tr></thead>
                            <tbody>$rows</tbody>
                        </table>
                        <div class="buttons">
                            <a href="/courts/${court.courtID}">Back to Court</a>
                            <a href="/">Home</a>
                        </div>
                    </body>
                    </html>
                """.trimIndent()

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        }
    )
}
