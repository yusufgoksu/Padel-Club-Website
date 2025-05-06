package pages

import models.User
import models.Rental
import services.UserServices
import services.RentalServices
import services.CourtServices
import services.ClubServices
import org.http4k.core.*
import org.http4k.routing.*

fun userPages(): RoutingHttpHandler = routes(
    "/users" bind Method.GET to {
        val users = UserServices.getAllUsers()
        val rows = users.joinToString("\n") { u ->
            """<tr>
           <td>${u.userID}</td>
           <td>${u.name}</td>
           <td>${u.email}</td>
           <td><a href="/users/${u.userID}">Details</a></td>
         </tr>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Users</title></head><body>
        <h1>Users List</h1>
        <table border="1" cellpadding="8">
          <tr><th>ID</th><th>Name</th><th>Email</th><th></th></tr>
          $rows
        </table>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/users/{userID}" bind Method.GET to bind@{ req ->
        val id = req.path("userID") ?: return@bind Response(Status.BAD_REQUEST)
        val u  = UserServices.getUserById(id) ?: return@bind Response(Status.NOT_FOUND)
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>${u.name}</title></head><body>
        <h1>User: ${u.name}</h1>
        <p>ID: ${u.userID}</p>
        <p>Email: ${u.email}</p>
        <a href="/users/${u.userID}/rentals">View Rentals</a><br>
        <a href="/users">← Back to Users</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    },

    "/users/{userID}/rentals" bind Method.GET to bind@{ req ->
        val id = req.path("userID") ?: return@bind Response(Status.BAD_REQUEST)
        val rentals = RentalServices.getRentalsForUser(id)
        val rows = rentals.joinToString("\n") { r ->
            val court = CourtServices.getCourtById(r.courtId)!!
            val club  = ClubServices.getClubById(court.clubId)!!
            """<tr>
           <td>${r.rentalID}</td>
           <td>${club.name}</td>
           <td>${court.name}</td>
           <td>${r.startTime}</td>
           <td><a href="/rentals/${r.rentalID}">View</a></td>
         </tr>"""
        }
        val html = """
      <!DOCTYPE html>
      <html><head><meta charset="UTF-8"><title>Rentals for $id</title></head><body>
        <h1>Rentals of User: $id</h1>
        <table border="1" cellpadding="8">
          <tr><th>ID</th><th>Club</th><th>Court</th><th>Start</th><th></th></tr>
          $rows
        </table>
        <a href="/users/$id">← Back to Details</a><br>
        <a href="/">Home</a>
      </body></html>
    """.trimIndent()
        Response(Status.OK).header("Content-Type","text/html").body(html)
    }
)
