package api

import models.*
import services.UserServices
import services.RentalServices
import services.CourtServices
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.*
import org.http4k.routing.*

fun usersWebApi(): RoutingHttpHandler {

    val userLens      = Body.auto<User>().toLens()
    val usersLens     = Body.auto<List<User>>().toLens()
    val userIdPathLens = Path.int().of("userID")

    return routes(
        // Tüm kullanıcıları JSON olarak döndür
        "/users" bind Method.GET to {
            Response(Status.OK).with(usersLens of UserServices.getAllUsers())
        },

        // Yeni kullanıcı oluştur
        "/users" bind Method.POST to { request ->
            try {
                val userReq = userLens(request)
                val created = UserServices.addUser(userReq.name, userReq.email)
                Response(Status.CREATED).with(userLens of created)
            } catch (e: IllegalArgumentException) {
                Response(Status.BAD_REQUEST).body(e.message ?: "Invalid input")
            }
        },

        // ID ile kullanıcı getir (HTML Detay)
        "/users/{userID}" bind Method.GET to { request ->
            val userId = userIdPathLens(request)
            val user = UserServices.getUserById(userId)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")

            val html = """
                <html>
                <head><meta charset="UTF-8"><title>User Details</title>
                <style>
                body { font-family: Arial; padding:20px }
                table { border:1px solid #ccc; border-collapse:collapse; width:50% }
                th,td { border:1px solid #ccc; padding:8px }
                </style>
                </head>
                <body>
                  <h1>User Details</h1>
                  <table>
                    <tr><th>ID</th><td>${user.userId}</td></tr>
                    <tr><th>Name</th><td>${user.name}</td></tr>
                    <tr><th>Email</th><td>${user.email}</td></tr>
                  </table>
                  <br/>
                  <a href="/users/$userId/rentals">View Rentals</a> |
                  <a href="/">Home</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Kullanıcıya ait kiralamaları HTML tablo olarak döndür
        "/users/{userID}/rentals" bind Method.GET to { request ->
            val userId = userIdPathLens(request)
            val rentals = RentalServices.getRentalsForUser(userId)
            val rows = rentals.joinToString("\n") { r ->
                val court = CourtServices.getCourtById(r.courtId)
                val club  = court?.let { ClubServices.getClubById(it.clubId) }
                """
                <tr>
                  <td>${r.rentalID}</td>
                  <td>${club?.name ?: r.clubId}</td>
                  <td>${court?.name ?: r.courtId}</td>
                  <td>${r.startTime}</td>
                  <td>${r.duration}</td>
                  <td><a href="/rentals/${r.rentalID}">Details</a></td>
                </tr>
                """.trimIndent()
            }

            val html = """
                <html>
                <head><meta charset="UTF-8"><title>Rentals for User $userId</title>
                <style>
                body { font-family: Arial; padding:20px }
                table { border:1px solid #ccc; border-collapse:collapse; width:80% }
                th,td { border:1px solid #ccc; padding:8px }
                </style>
                </head>
                <body>
                  <h1>Rentals for User $userId</h1>
                  <table>
                    <tr><th>Rental</th><th>Club</th><th>Court</th><th>Start</th><th>Duration</th><th></th></tr>
                    $rows
                  </table>
                  <br/>
                  <a href="/users/$userId">Back to User</a> |
                  <a href="/">Home</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // E-posta ile kullanıcı getir
        "/users/by-email" bind Method.GET to { request ->
            val email = request.query("email")
                ?: return@to Response(Status.BAD_REQUEST).body("Missing email")
            val user = UserServices.getUserByEmail(email)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")
            Response(Status.OK).with(userLens of user)
        },

        // Kullanıcı token üret
        "/users/{userID}/token" bind Method.POST to { request ->
            val userId = userIdPathLens(request)
            val token = UserServices.generateUserToken(userId)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")
            Response(Status.OK).body("Token: ${token.first}, UserID: ${token.second}")
        },

        // Yedek: Tüm kullanıcıları JSON ile döndür
        "/users/all" bind Method.GET to {
            Response(Status.OK).with(usersLens of UserServices.getAllUsers())
        }
    )
}
