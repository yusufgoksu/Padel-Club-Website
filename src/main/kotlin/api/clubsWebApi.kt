package api

import models.*
import services.ClubServices
import services.UserServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun clubsWebApi(): RoutingHttpHandler {

    val clubLens = Body.auto<Club>().toLens()
    val clubsLens = Body.auto<List<Club>>().toLens()

    return routes(
        // Tüm kulüpleri listele (JSON formatında)
        "/clubs/json" bind Method.GET to {
            Response(Status.OK).with(clubsLens of ClubServices.getAllClubs())
        },

        // Yeni kulüp oluştur
        "/clubs" bind Method.POST to { request ->
            val club = clubLens(request)
            val createdClub = ClubServices.addClub(club.name, club.ownerUid)
            Response(Status.CREATED).with(clubLens of createdClub)
        },

        // Kulüp detaylarını getirme (JSON formatında)
        "/clubs/{clubID}/json" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val club = ClubServices.getClubById(clubID) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(clubLens of club)
        },

        // Tüm kulüpleri listele (HTML formatında)
        "/clubs" bind Method.GET to {
            val clubs = ClubServices.getAllClubs()

            val html = buildString {
                append("""
                    <!DOCTYPE html>
                    <html>
                    <head><title>Clubs List</title></head>
                    <body>
                        <h1>Clubs List</h1>
                        <ul>
                """.trimIndent())

                for (club in clubs) {
                    append("<li><a href=\"/clubs/details/${club.clubID}\">${club.name}</a></li>")
                }

                append("""
                        </ul>
                        <a href="/">Back to Home</a>
                    </body>
                    </html>
                """.trimIndent())
            }

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        },

        // Kulüp detayları (HTML formatında)
        "/clubs/details/{clubID}" bind Method.GET to { request ->
            val clubID = request.path("clubID") ?: return@to Response(Status.BAD_REQUEST)
            val club = ClubServices.getClubById(clubID) ?: return@to Response(Status.NOT_FOUND)

            val html = """
                <!DOCTYPE html>
                <html>
                <head><title>Club Details</title></head>
                <body>
                    <h1>Club Details</h1>
                    <p><strong>Name:</strong> ${club.name}</p>
                    <p><strong>Owner UID:</strong> ${club.ownerUid}</p>
                    
                    <!-- Kulüp sahibine ait detaylara yönlendiren buton -->
                    <a href="/users/${club.ownerUid}" class="btn btn-primary">View User Details</a><br>
                    
                    <a href="/clubs">Back to Clubs List</a><br>
                    <a href="/">Back to Home</a><br>
                    
                    <!-- Kortlar sayfasına yönlendiren buton -->
                    <a href="/courts/club/${club.clubID}">View Courts</a>
                </body>
                </html>
            """.trimIndent()

            Response(Status.OK).body(html).header("Content-Type", "text/html")
        }
    )
}
