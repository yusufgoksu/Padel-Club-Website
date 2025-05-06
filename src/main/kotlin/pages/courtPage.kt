package pages

import models.Court
import services.CourtServices
import services.ClubServices
import services.RentalServices
import org.http4k.core.*
import org.http4k.routing.*

fun courtPages(): RoutingHttpHandler = routes(
    "/courts" bind Method.GET to {
        val courts = CourtServices.getAllCourts()
        val rows = courts.joinToString("\n") { c ->
            """<tr>
           <td>${c.name}</td>
           <td>${c.clubId}</td>
           <td><a href="/courts/${c.courtID}">View</a></td>
         </tr>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>All Courts</title></head><body>
        <h1>All Courts</h1>
        <table border="1" cellpadding="8">
          <tr><th>Name</th><th>Club ID</th><th>Details</th></tr>
          $rows
        </table>
        <a href="/">← Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/courts/{courtID}" bind Method.GET to bind@{ req ->
        val id = req.path("courtID") ?: return@bind Response(Status.BAD_REQUEST)
        val court = CourtServices.getCourtById(id) ?: return@bind Response(Status.NOT_FOUND)
        val club = ClubServices.getClubById(court.clubId) ?: return@bind Response(Status.NOT_FOUND)
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Court Details</title></head><body>
        <h1>Court: ${court.name}</h1>
        <p><strong>Club:</strong> ${club.name}</p>
        <a href="/courts/${id}/rentals">View Rentals</a><br>
        <a href="/courts">← Back to Courts</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/courts/club/{clubId}" bind Method.GET to bind@{ req ->
        val clubId = req.path("clubId") ?: return@bind Response(Status.BAD_REQUEST)
        val courts = CourtServices.getCourtsForClub(clubId)
        val items = courts.joinToString("\n") { c ->
            """<li><a href="/courts/${c.courtID}">${c.name}</a></li>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Courts in Club</title></head><body>
        <h1>Courts of Club $clubId</h1>
        <ul>$items</ul>
        <a href="/clubs">← Back to Clubs</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/courts/{courtID}/rentals" bind Method.GET to bind@{ req ->
        val id = req.path("courtID") ?: return@bind Response(Status.BAD_REQUEST)
        val court = CourtServices.getCourtById(id) ?: return@bind Response(Status.NOT_FOUND)
        val rentals = RentalServices.getRentalsForClubAndCourt(court.clubId, id, null)
        val rows = rentals.joinToString("\n") { r ->
            """<tr>
           <td>${r.startTime}</td>
           <td>${r.duration}</td>
           <td>${r.userId}</td>
           <td><a href="/rentals/${r.rentalID}">View</a></td>
         </tr>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Rentals of ${court.name}</title></head><body>
        <h1>Rentals for Court: ${court.name}</h1>
        <table border="1" cellpadding="8">
          <tr><th>Start</th><th>Duration</th><th>User</th><th></th></tr>
          $rows
        </table>
        <a href="/courts/${id}">← Back to Court</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    }
)
