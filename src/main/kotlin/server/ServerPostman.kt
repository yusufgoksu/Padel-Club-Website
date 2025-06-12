package server

import api.clubsWebApi
import api.courtsWebApi
import api.rentalsWebApi
import api.usersWebApi
import main.addTestDataToDatabase
import main.spaAndStatic
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    val PORT = 9000

    val app = routes(
        clubsWebApi(),
        usersWebApi(),
        courtsWebApi(),
        rentalsWebApi(),
        spaAndStatic()
    )

    app.asServer(SunHttp(PORT)).start().also {
        println("🚀 Server running on http://localhost:$PORT")
    }
}