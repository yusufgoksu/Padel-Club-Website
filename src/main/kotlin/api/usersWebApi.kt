package api

import models.*
import services.UserServices
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.*

fun usersWebApi(): HttpHandler {
    val userLens = Body.auto<User>().toLens()
    val usersLens = Body.auto<List<User>>().toLens()

    return routes(
        "/users" bind Method.GET to {
            Response(Status.OK).with(usersLens of UserServices.getUsers())
        },
        "/users" bind Method.POST to { request ->
            val user = userLens(request)
            val createdUser = UserServices.addUser(user.name, user.email)
            Response(Status.CREATED).with(userLens of createdUser)
        },
        "/users/{uid}" bind Method.GET to { request ->
            val uid = request.path("uid") ?: return@to Response(Status.BAD_REQUEST)
            val user = UserServices.getUserById(uid) ?: return@to Response(Status.NOT_FOUND)
            Response(Status.OK).with(userLens of user)
        }
    )
}
