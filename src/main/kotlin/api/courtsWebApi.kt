package api

import models.*
import services.*
import org.http4k.core.*
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {
    return routes(
        // Tüm kortları listele
        "/courts" bind Method.GET to {
            val courts = CourtServices.getAllCourts()
            val html = buildString {
                append("""
                    <html>
                    <head>
                        <title>All Courts</title>
                        <style>
                            body { font-family: Arial, sans-serif; padding: 20px; }
                            h1 { color: #333; }
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
                """)
                courts.forEach {
                    append("<tr><td>${it.name}</td><td>${it.clubId}</td><td><a href='/courts/${it.courtID}'>View</a></td></tr>")
                }
                append("""
                        </table>
                        <br><a href='/'>Back to Home</a>
                    </body>
                    </html>
                """)
            }
            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        // Belirli bir kortun detayları
        "/courts/{courtID}" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)
            val club = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)

            val html = """
                <html>
                <head>
                    <title>Court Details</title>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        h1, h2 { color: #333; }
                        a {
                            display: inline-block;
                            margin-top: 20px;
                            margin-right: 10px;
                            text-decoration: none;
                            color: #007bff;
                        }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Court: ${court.name}</h1>
                    <p><b>Club:</b> ${club.name}</p>
                    <p><b>Club Owner:</b> ${club.ownerUid}</p>

                    <a href="/courts/${courtID}/rentals">View Rentals</a><br>
                    <a href="/courts/club/${club.clubID}">Back to Courts in Club</a>
                    <a href="/">Back to Home</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        // Belirli bir kulübe ait tüm kortları listele
        "/courts/club/{clubId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            val courts = CourtServices.getCourtsForClub(clubId)
            val html = buildString {
                append("""
                    <html>
                    <head><title>Courts in Club</title></head>
                    <body>
                        <h1>Courts in Club</h1>
                        <ul>
                """)
                courts.forEach {
                    append("<li><a href='/courts/${it.courtID}'>${it.name}</a></li>")
                }
                append("""
                        </ul>
                        <a href='/clubs'>Back to Clubs</a>
                        <a href='/'>Back to Home</a>
                    </body>
                    </html>
                """)
            }
            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        // Kiralamaları listele
        "/courts/{courtID}/rentals" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)
            val clubId = court.clubId
            val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtID)

            val html = buildString {
                append("""
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <title>Rentals for Court: ${court.name}</title>
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
                        <h1>Rentals for Court: ${court.name}</h1>
                        <table>
                            <thead>
                                <tr>
                                    <th>Start Time</th>
                                    <th>Duration (hours)</th>
                                    <th>User ID</th>
                                    <th>Details</th>
                                </tr>
                            </thead>
                            <tbody>
                """)
                rentals.forEach {
                    append("""
                        <tr>
                            <td>${it.startTime}</td>
                            <td>${it.duration}</td>
                            <td>${it.userId}</td>
                            <td><a href="/rentals/${it.rentalID}">Rental Details</a></td>
                        </tr>
                    """)
                }
                append("""
                            </tbody>
                        </table>
                        <br>
                        <a href="/courts/${courtID}">Back to Court</a>
                        <a href="/">Back to Home</a>
                    </body>
                    </html>
                """)
            }

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        }
    )
}
