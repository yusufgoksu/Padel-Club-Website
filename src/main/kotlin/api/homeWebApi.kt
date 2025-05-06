package api

import org.http4k.core.*
import org.http4k.routing.*
import org.http4k.core.Response
import org.http4k.core.Status

fun homeWebApi(): RoutingHttpHandler = routes(
    "/" bind Method.GET to { _: Request ->
        Response(Status.OK).body("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Home</title>
            </head>
            <body>
                <h1>Welcome to the Padel Club System</h1>
                <ul>
                    <li><a href="/clubs">Go to Clubs List</a></li>
                </ul>
            </body>
            </html>
        """.trimIndent())
    }
)
