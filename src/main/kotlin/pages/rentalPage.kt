package pages

import models.Rental
import services.RentalServices
import services.CourtServices
import services.ClubServices
import services.UserServices
import org.http4k.core.*
import org.http4k.routing.*

fun rentalPages(): RoutingHttpHandler = routes(
    "/rentals" bind Method.GET to {
        val rentals = RentalServices.getRentals()
        val rows = rentals.joinToString("\n") { r ->
            """<tr>
           <td>${r.rentalID}</td>
           <td>${r.startTime}</td>
           <td>${r.duration}</td>
           <td><a href="/rentals/${r.rentalID}">View</a></td>
         </tr>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>All Rentals</title></head><body>
        <h1>All Rentals</h1>
        <table border="1" cellpadding="8">
          <tr><th>ID</th><th>Start</th><th>Duration</th><th></th></tr>
          $rows
        </table>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/rentals/{rentalID}" bind Method.GET to bind@{ req ->
        val id = req.path("rentalID") ?: return@bind Response(Status.BAD_REQUEST)
        val r  = RentalServices.getRentalById(id) ?: return@bind Response(Status.NOT_FOUND)
        val court = CourtServices.getCourtById(r.courtId)!!
        val club  = ClubServices.getClubById(court.clubId)!!
        val user  = UserServices.getUserById(r.userId)!!
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Rental $id</title></head><body>
        <h1>Rental Details</h1>
        <p><strong>ID:</strong> ${r.rentalID}</p>
        <p><strong>User:</strong> ${user.name} (${user.userID})</p>
        <p><strong>Club:</strong> ${club.name}</p>
        <p><strong>Court:</strong> ${court.name}</p>
        <p><strong>Start:</strong> ${r.startTime}</p>
        <p><strong>Duration:</strong> ${r.duration} hrs</p>
        <a href="/users/${user.userID}/rentals">← Back to User Rentals</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    }
)
