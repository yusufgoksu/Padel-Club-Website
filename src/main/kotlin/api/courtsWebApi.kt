package api

import models.Court
import models.Rental
import services.ClubServices
import services.CourtServices
import services.RentalServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {

    val courtLens   = Body.auto<Court>().toLens()
    val courtsLens  = Body.auto<List<Court>>().toLens()
    val rentalsLens = Body.auto<List<Rental>>().toLens()

    return routes(
        // — JSON endpoints —

        // Tüm kortları JSON formatında listele
        "/courts/json" bind Method.GET to {
            Response(Status.OK)
                .with(courtsLens of CourtServices.getAllCourts())
        },

        // Tek bir kortu JSON olarak getir
        "/courts/{courtID}/json" bind Method.GET to { req ->
            val id = req.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtById(id)
                ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(courtLens of court)
        },

        // — HTML endpoints —

        // Courts List — Clubs List stiliyle
        "/courts" bind Method.GET to {
            val courts = CourtServices.getAllCourts()
            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Courts List</title>
                      <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        ul { list-style: none; padding: 0; }
                        li { margin: 8px 0; }
                        a { color: #007bff; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                      </style>
                    </head>
                    <body>
                      <h1>Courts List</h1>
                      <ul>
                """.trimIndent())
                for (c in courts) {
                    append("""<li><a href="/courts/${c.courtID}">${c.name}</a></li>""")
                }
                append("""
                      </ul>
                      <br>
                      <a href="/">Back to Home</a>
                    </body>
                    </html>
                """.trimIndent())
            }
            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Court Details — Clubs Details stiliyle
        "/courts/{courtID}" bind Method.GET to { req ->
            val id = req.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtById(id)
                ?: return@to Response(Status.NOT_FOUND)
            val club = ClubServices.getClubById(court.clubId)
                ?: return@to Response(Status.NOT_FOUND)

            val html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <title>Court Details</title>
                  <style>
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    p { margin: 8px 0; }
                    a { display: inline-block; margin: 8px 0; color: #007bff; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                  </style>
                </head>
                <body>
                  <h1>Court: ${court.name}</h1>
                  <p><strong>Court ID:</strong> ${court.courtID}</p>
                  <p><strong>Club:</strong> ${club.name}</p>
                  <p><strong>Owner UID:</strong> ${club.ownerUid}</p>
                  <br>
                  <!-- Kiralamaları gör -->
                  <a href="/courts/${court.courtID}/rentals">View Rentals</a><br>
                  <!-- Geri linkler -->
                  <a href="/courts">← Back to Courts List</a><br>
                  <a href="/">Home</a><br>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Courts in Club — Clubs List stiliyle
        "/courts/club/{clubId}" bind Method.GET to { req ->
            val clubId = req.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            val club   = ClubServices.getClubById(clubId)
                ?: return@to Response(Status.NOT_FOUND)
            val courts = CourtServices.getCourtsForClub(clubId)

            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Courts in ${club.name}</title>
                      <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        ul { list-style: none; padding: 0; }
                        li { margin: 8px 0; }
                        a { color: #007bff; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                      </style>
                    </head>
                    <body>
                      <h1>Courts in ${club.name}</h1>
                      <ul>
                """.trimIndent())
                for (c in courts) {
                    append("""<li><a href="/courts/${c.courtID}">${c.name}</a></li>""")
                }
                append("""
                      </ul>
                      <br>
                      <a href="/clubs">← Back to Clubs</a>
                      <a href="/">Back to Home</a>
                    </body>
                    </html>
                """.trimIndent())
            }

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Rentals for a Court
        "/courts/{courtID}/rentals" bind Method.GET to { req ->
            val courtID = req.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            val court   = CourtServices.getCourtById(courtID)
                ?: return@to Response(Status.NOT_FOUND)
            val rentals = RentalServices.getRentalsForClubAndCourt(court.clubId, courtID)

            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Rentals for ${court.name}</title>
                      <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        table { border-collapse: collapse; width: 100%; }
                        th, td { border: 1px solid #ddd; padding: 8px; }
                        th { background: #f2f2f2; }
                        a { color: #007bff; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                      </style>
                    </head>
                    <body>
                      <h1>Rentals for ${court.name}</h1>
                      <table>
                        <tr><th>Start Time</th><th>Duration</th><th>User ID</th><th></th></tr>
                """.trimIndent())
                rentals.forEach { r ->
                    append("""
                        <tr>
                          <td>${r.startTime}</td>
                          <td>${r.duration}</td>
                          <td>${r.userId}</td>
                          <td><a href="/rentals/${r.rentalID}">Details</a></td>
                        </tr>
                    """.trimIndent())
                }
                append("""
                      </table>
                      <br>
                      <a href="/courts/$courtID">← Back to Court</a><br>
                      <a href="/">Home</a>
                    </body>
                    </html>
                """.trimIndent())
            }

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        }
    )
}
