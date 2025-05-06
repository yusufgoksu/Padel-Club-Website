package pages

import models.Club
import services.ClubServices
import services.UserServices
import org.http4k.core.*
import org.http4k.routing.*

fun clubPages(): RoutingHttpHandler = routes(

    // Clubs List sayfası (HTML)
    "/clubs" bind Method.GET to { req ->
        val clubs: List<Club> = ClubServices.getAllClubs()
        val html = buildString {
            append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset=\"UTF-8\">
                    <title>Clubs List</title>
                    <style>
                      body { font-family: Arial, sans-serif; padding: 20px; }
                      ul { list-style: none; padding: 0; }
                      li { margin: 8px 0; }
                      a { text-decoration: none; color: #007bff; }
                      a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Clubs List</h1>
                    <ul>
            """.trimIndent())
            clubs.forEach { club ->
                append("<li><a href=\"/clubs/details/${club.clubID}\">${club.name}</a></li>\n")
            }
            append("""
                    </ul>
                    <a href=\"/\">Back to Home</a>
                </body>
                </html>
            """.trimIndent())
        }
        Response(Status.OK)
            .header("Content-Type", "text/html")
            .body(html)
    },

    // Club Details sayfası (HTML)
    "/clubs/details/{clubID}" bind Method.GET to { req ->
        val clubID = req.path("clubID")
            ?: return@to Response(Status.BAD_REQUEST).body("Missing clubID")
        val club = ClubServices.getClubById(clubID)
            ?: return@to Response(Status.NOT_FOUND).body("Club not found")

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset=\"UTF-8\">
                <title>Club Details</title>
                <style>
                  body { font-family: Arial, sans-serif; padding: 20px; }
                  p { margin: 8px 0; }
                  a { text-decoration: none; color: #007bff; margin-right: 10px; }
                  a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <h1>Club Details</h1>
                <p><strong>Name:</strong> ${club.name}</p>
                <p><strong>Owner UID:</strong> ${club.ownerUid}</p>

                <a href=\"/users/${club.ownerUid}\">View User Details</a><br>
                <a href=\"/clubs\">Back to Clubs List</a><br>
                <a href=\"/\">Back to Home</a><br>
                <a href=\"/courts/club/${club.clubID}\">View Courts</a>
            </body>
            </html>
        """.trimIndent()

        Response(Status.OK)
            .header("Content-Type", "text/html")
            .body(html)
    }
)
