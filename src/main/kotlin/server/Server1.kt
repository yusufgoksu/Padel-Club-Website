package main

import api.*

import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {


    // 2) HTML sayfa rotaları (root altında)
    val pageRoutes = routes(
        homeWebApi(),
        clubsWebApi(),
        courtsWebApi(),
        rentalsWebApi(),
        usersWebApi()
    )

    // 3) Hepsini birleştir ve sunucuyu başlat
    val app = routes(pageRoutes)
    app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")


}
