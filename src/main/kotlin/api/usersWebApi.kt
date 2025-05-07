package api

import models.*
import services.UserServices
import services.RentalServices
import services.CourtServices
import services.ClubServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun usersWebApi(): RoutingHttpHandler {

    val userLens    = Body.auto<User>().toLens()
    val usersLens   = Body.auto<List<User>>().toLens()
    val rentalsLens = Body.auto<List<Rental>>().toLens()

    return routes(

        // Tüm kullanıcıları döndür (getUsers)
        "/users" bind Method.GET to {
            Response(Status.OK).with(usersLens of UserServices.getAllUsers())
        },

        // Yeni kullanıcı oluştur
        "/users" bind Method.POST to { request ->
            try {
                val user = userLens(request)
                val createdUser = UserServices.addUser(user.name, user.email)
                Response(Status.CREATED).with(userLens of createdUser)
            } catch (e: IllegalArgumentException) {
                Response(Status.BAD_REQUEST).body(e.message ?: "Invalid input")
            }
        },

        // ID ile kullanıcı getir (HTML Detay Sayfası)
        "/users/{userID}" bind Method.GET to { request ->
            val userID = request.path("userID")
                ?: return@to Response(Status.BAD_REQUEST).body("Missing userID")
            val user = UserServices.getUserById(userID)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")

            val html = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>User Details</title>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        h1 { color: #333; }
                        table { border-collapse: collapse; width: 60%; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
                        th { background-color: #f2f2f2; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        a { text-decoration: none; color: #007bff; margin-right: 10px; }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>User Details</h1>
                    <table>
                        <tr><th>User ID</th><td>${user.userId}</td></tr>
                        <tr><th>Name</th><td>${user.name}</td></tr>
                        <tr><th>Email</th><td>${user.email}</td></tr>
                        <tr><th>Token</th><td>${user.token}</td></tr>
                    </table>
                    <br>
                    <a href="/clubs">Back to Clubs</a>
                    <a href="/">Back to Home</a>
                    <a href="/users/${user.userId}/rentals">View Rentals</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK)
                .header("Content-Type", "text/html")
                .body(html)
        },

        // Kullanıcıya ait kiralamaları HTML tablo olarak döndür
        "/users/{userID}/rentals" bind Method.GET to { request ->
            val userID = request.path("userID")
                ?: return@to Response(Status.BAD_REQUEST).body("Missing userID")

            val rentals = RentalServices.getRentalsForUser(userID)

            // Satırları hazırla, son sütunda Details
            val rowsHtml = rentals.joinToString("\n") { r ->
                val court = CourtServices.getCourtById(r.courtId)
                val club  = court?.let { ClubServices.getClubById(it.clubId) }
                """
                <tr>
                  <td>${r.rentalID}</td>
                  <td>${club?.name ?: r.clubId}</td>
                  <td>${court?.name ?: r.courtId}</td>
                  <td>${r.startTime}</td>
                  <td>${r.duration}</td>
                  <td><a href="/rentals/${r.rentalID}">Rental Details</a></td>
                </tr>
                """.trimIndent()
            }

            val html = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Rentals for User $userID</title>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        h1 { color: #333; }
                        table { border-collapse: collapse; width: 80%; margin-top: 20px; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #f2f2f2; }
                        tr:nth-child(even) { background-color: #f9f9f9; }
                        a { text-decoration: none; color: #007bff; }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <h1>Rentals for User: $userID</h1>
                    <table>
                        <thead>
                          <tr>
                            <th>Rental ID</th>
                            <th>Club</th>
                            <th>Court</th>
                            <th>Start Time</th>
                            <th>Duration (hrs)</th>
                            <th>Details</th>
                          </tr>
                        </thead>
                        <tbody>
                          $rowsHtml
                        </tbody>
                    </table>
                    <br>
                    <a href="/users/$userID">← Back to User Details</a>
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

        // Kullanıcı token'ı üret
        "/users/{userID}/token" bind Method.POST to { request ->
            val userID = request.path("userID")
                ?: return@to Response(Status.BAD_REQUEST).body("Missing userID")
            val token = UserServices.generateUserToken(userID)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")
            Response(Status.OK).body("Token: ${token.first}, UserID: ${token.second}")
        },

        // Tüm kullanıcıları döndür (getAllUsers)
        "/users/all" bind Method.GET to {
            val allUsers = UserServices.getAllUsers()
            Response(Status.OK).with(usersLens of allUsers)
        }
    )
}