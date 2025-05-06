package pages

import models.Club
import services.ClubServices
import org.http4k.core.*
import org.http4k.routing.*

fun clubPages(): RoutingHttpHandler = routes(
    "/clubs" bind Method.GET to {
        val clubs = ClubServices.getAllClubs()
        val items = clubs.joinToString("\n") { c ->
            """<li><a href="/clubs/details/${c.clubID}">${c.name}</a></li>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Clubs List</title></head><body>
        <h1>Clubs List</h1>
        <ul>$items</ul>
        <a href="/">← Back to Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/clubs/details/{clubID}" bind Method.GET to bind@{ req ->
        val id = req.path("clubID") ?: return@bind Response(Status.BAD_REQUEST)
        val club = ClubServices.getClubById(id) ?: return@bind Response(Status.NOT_FOUND)
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Club Details</title></head><body>
        <h1>${club.name}</h1>
        <p><strong>Owner:</strong> ${club.ownerUid}</p>
        <a href="/users/${club.ownerUid}">View Owner Details</a><br>
        <a href="/clubs">← Back to Clubs</a><br>
        <a href="/">Home</a><br>
        <a href="/courts/club/${club.clubID}">View Courts</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    }
)
