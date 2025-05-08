package api

import models.Club
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.int
import org.http4k.routing.*

fun clubsWebApi(): RoutingHttpHandler {

    val clubLens  = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()
    val clubIdPath = Path.int().of("clubID")

    return routes(
        // — JSON endpoints —

        // Tüm kulüpleri JSON formatında listele
        "/clubs/json" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getAllClubs())
        },

        // Yeni kulüp oluştur (JSON)
        "/clubs" bind Method.POST to { req ->
            val clubReq = clubLens(req)
            val created = ClubServices.addClub(
                name   = clubReq.name,
                userID = clubReq.userID
            )
            Response(Status.CREATED).with(clubLens of created)
        },

        // Tek bir kulübü JSON olarak getir
        "/clubs/{clubID}/json" bind Method.GET to { req ->
            val id = clubIdPath(req)
            val club = ClubServices.getClubById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Club not found")
            Response(Status.OK).with(clubLens of club)
        },

        // — HTML endpoints —

        // Tüm kulüpleri HTML formatında listele
        "/clubs" bind Method.GET to {
            val clubs = ClubServices.getAllClubs()
            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                      <meta charset="UTF-8">
                      <title>Clubs List</title>
                      <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        ul { list-style: none; padding: 0; }
                        li { margin: 8px 0; }
                        a { color: #007bff; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                      </style>
                    </head>
                    <body>
                      <h1>Clubs List</h1>
                      <ul>
                """.trimIndent())
                clubs.forEach { c ->
                    append("""<li><a href="/clubs/details/${c.clubID}">${c.name}</a></li>""")
                }
                append("""
                      </ul>
                      <br>
                      <a href="/">← Back to Home</a>
                    </body>
                    </html>
                """.trimIndent())
            }
            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Bir kulübün detay sayfası (HTML)
        "/clubs/details/{clubID}" bind Method.GET to { req ->
            val id = clubIdPath(req)
            val club = ClubServices.getClubById(id)
                ?: return@to Response(Status.NOT_FOUND).body("Club not found")

            val html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <title>Club Details</title>
                  <style>
                    body { font-family: Arial, sans-serif; padding: 20px; }
                    p { margin: 8px 0; }
                    a { display: inline-block; margin: 8px 0; color: #007bff; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                  </style>
                </head>
                <body>
                  <h1>Club: ${club.name}</h1>
                  <p><strong>Club ID:</strong> ${club.clubID}</p>
                  <p><strong>Owner User ID:</strong> ${club.userID}</p>
                  <br>
                  <a href="/users/${club.userID}">View User Details</a><br>
                  <a href="/clubs">← Back to Clubs List</a><br>
                  <a href="/">Home</a><br>
                  <a href="/courts/club/${club.clubID}">View Courts</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        }
    )
}
