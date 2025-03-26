package server

import api.courtsWebApi
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val port = 9000
    val app = courtsWebApi()
    val server = app.asServer(Jetty(port)).start()
    println("Server running on port $port")
}
