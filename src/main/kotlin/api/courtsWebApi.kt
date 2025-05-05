package api

import models.*
import services.CourtServices
import org.http4k.core.*
import org.http4k.routing.*

fun courtsWebApi(): RoutingHttpHandler {

    return routes(
        // Tüm kortları listele
        "/courts" bind Method.GET to {
            Response(Status.OK).body("<h1>All Courts</h1>").header("Content-Type", "text/html")
        },

        // Yeni kort ekle
        "/courts" bind Method.POST to { request ->
            val court = request.bodyString()
            Response(Status.CREATED).body("Court Created: $court").header("Content-Type", "text/html")
        },

        // Kort ID'ye göre kortu getir
        "/courts/{courtID}" bind Method.GET to { request ->
            val courtID = request.path("courtID") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val court = CourtServices.getCourtById(courtID)
                Response(Status.OK).body("<h1>Court: ${court?.name}</h1>").header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kulüp ID'sine göre kortları listele (HTML)
        "/courts/club/{clubId}" bind Method.GET to { request ->
            val clubId = request.path("clubId") ?: return@to Response(Status.BAD_REQUEST)
            return@to try {
                val courtsForClub = CourtServices.getCourtsForClub(clubId)
                val html = buildString {
                    append("""
                        <!DOCTYPE html>
                        <html>
                        <head><title>Courts for Club</title></head>
                        <body>
                            <h1>Courts for Club</h1>
                            <ul>
                    """.trimIndent())

                    for (court in courtsForClub) {
                        append("<li>${court.name}</li>")
                    }

                    append("""
                            </ul>
                            <a href='/clubs'>Back to Clubs List</a><br>
                            <a href='/'>Back to Home</a>
                        </body>
                        </html>
                    """.trimIndent())
                }

                Response(Status.OK).body(html).header("Content-Type", "text/html")
            } catch (e: IllegalArgumentException) {
                Response(Status.NOT_FOUND)
            }
        },

        // Kort ismine göre arama
        "/courts/name/{courtName}" bind Method.GET to { request ->
            val courtName = request.path("courtName") ?: return@to Response(Status.BAD_REQUEST)
            val court = CourtServices.getCourtByName(courtName)
            return@to if (court != null) {
                Response(Status.OK).body("<h1>Court: ${court.name}</h1>").header("Content-Type", "text/html")
            } else {
                Response(Status.NOT_FOUND)
            }
        }
    )
}
