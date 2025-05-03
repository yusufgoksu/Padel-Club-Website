package api

import models.*
import services.UserServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun usersWebApi(): RoutingHttpHandler {

    val userLens = Body.auto<User>().toLens()
    val usersLens = Body.auto<List<User>>().toLens()

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

        // ID ile kullanıcı getir
        "/users/{userID}" bind Method.GET to { request ->
            val userID = request.path("userID") ?: return@to Response(Status.BAD_REQUEST)
            val user = UserServices.getUserById(userID) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(userLens of user)
        },

        // E-posta ile kullanıcı getir
        "/users/by-email" bind Method.GET to { request ->
            val email = request.query("email") ?: return@to Response(Status.BAD_REQUEST).body("Missing email")
            val user = UserServices.getUserByEmail(email)
                ?: return@to Response(Status.NOT_FOUND).body("User not found")
            Response(Status.OK).with(userLens of user)
        },

        // Kullanıcı token'ı üret
        "/users/{userID}/token" bind Method.POST to { request ->
            val userID = request.path("userID") ?: return@to Response(Status.BAD_REQUEST).body("Missing userID")
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
