package api

import models.*
import services.CourtServices
import services.RentalServices
import services.ClubServices
import org.http4k.core.*
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {
    return routes(
        // Tüm kortları listele
        "/courts" bind Method.GET to {
            Response(Status.OK).body("<h1>All Courts</h1>").header("Content-Type", "text/html")
        },

        // Yeni kort ekle
        "/courts" bind Method.POST to { request ->
            val court = request.bodyString()
            Response(Status.CREATED).body("Court Created: $court").header("Content-Type", "text/html")
        },

        // Kort ID'ye göre kortu getir
        "/courts/{courtID}" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                // Kortu al
                val court = CourtServices.getCourtById(courtID) ?: return@to Response(Status.NOT_FOUND)

                // Kortun ait olduğu kulübü al
                val club = ClubServices.getClubById(court.clubId) ?: return@to Response(Status.NOT_FOUND)

                // Bu kort ve kulüp için mevcut dolu kiralamaları al
                val rentals = RentalServices.getRentalsForClubAndCourt(club.clubID, court.courtID)

                // Dolu olan kiralama saatlerini listele
                val rentalTimes = rentals.joinToString("<br>") {
                    "Start: ${it.startTime}, Duration: ${it.duration} hours"
                }

                // HTML yanıtı oluştur
                val html = buildString {
                    append("<h1>Court: ${court.name}</h1>")
                    append("<p><b>Club: ${club.name}</b></p>")
                    append("<p><b>Owner: ${club.ownerUid}</b></p>")
                    append("<h3>Booked Rentals:</h3>")
                    append("<p>$rentalTimes</p>")
                    append("<br><a href='/rentals/court/${court.courtID}'>View Detailed Rentals</a><br>")
                    append("""
                        <br><a href='/courts/club/${club.clubID}'>Back to Courts in Club</a><br>
                        <a href='/clubs'>Back to Clubs List</a><br>
                        <a href='/'>Back to Home</a>
                    """)
                }

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kulüp ID'sine göre kortları listele (HTML)
        "/courts/club/{clubId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val courtsForClub = CourtServices.getCourtsForClub(clubId)
                val html = buildString {
                    append("""
                        <!DOCTYPE html>
                        <html>
                        <head><title>Courts for Club</title></head>
                        <body>
                            <h1>Courts for Club</h1>
                            <ul>
                    """.trimIndent())

                    for (court in courtsForClub) {
                        append("<li><a href='/courts/${court.courtID}'>${court.name}</a></li>")
                    }

                    append("""
                            </ul>
                            <a href='/clubs'>Back to Clubs List</a><br>
                            <a href='/'>Back to Home</a>
                        </body>
                        </html>
                    """.trimIndent())
                }

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kort ismine göre arama
        "/courts/name/{courtName}" bind Method.GET to { request ->
            val courtName = request.path("courtName") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtByName(courtName)
            return@to if (court != null) {
                Response(Status.OK).body("<h1>Court: ${court.name}</h1>").header("Content-Type", "text/html")
            } else {
                Response(Status.NOT_FOUND)
            }
        },

        // CourtID'ye göre kiralama listesini göster (detaylı)
        "/rentals/court/{courtId}" bind Method.GET to { request ->
            val courtId = request.path("courtId") ?: return@to Response(Status.BAD_REQUEST)

            try {
                val court = CourtServices.getCourtById(courtId) ?: return@to Response(Status.NOT_FOUND)
                val clubId = court.clubId

                val rentals = RentalServices.getRentalsForClubAndCourt(clubId, courtId)

                val html = buildString {
                    append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Rentals for ${court.name}</title>
                    <style>
                        table {
                            border-collapse: collapse;
                            width: 80%;
                            margin: 20px 0;
                        }
                        th, td {
                            border: 1px solid #ddd;
                            padding: 8px;
                            text-align: left;
                        }
                        th {
                            background-color: #f2f2f2;
                        }
                        tr:nth-child(even) {
                            background-color: #f9f9f9;
                        }
                    </style>
                </head>
                <body>
                    <h1>Rentals for Court: ${court.name}</h1>
            """.trimIndent())

                    if (rentals.isEmpty()) {
                        append("<p>No rentals found for this court.</p>")
                    } else {
                        append("""
                    <table>
                        <thead>
                            <tr>
                                <th>Start Time</th>
                                <th>Duration (hours)</th>
                                <th>User ID</th>
                            </tr>
                        </thead>
                        <tbody>
                """.trimIndent())

                        for (rental in rentals) {
                            append("""
                        <tr>
                            <td>${rental.startTime}</td>
                            <td>${rental.duration}</td>
                            <td>${rental.userId}</td>
                        </tr>
                    """.trimIndent())
                        }

                        append("""
                        </tbody>
                    </table>
                """.trimIndent())
                    }

                    append("""
                    <br><a href='/courts/$courtId'>Back to Court</a><br>
                    <a href='/'>Back to Home</a>
                </body>
                </html>
            """.trimIndent())
                }

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: Exception) {
                Response(Status.INTERNAL_SERVER_ERROR).body("Error loading rentals.").header("Content-Type", "text/html")
            }
        }
    )
}
