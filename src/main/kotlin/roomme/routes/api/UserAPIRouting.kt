package roomme.routes.api

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import roomme.plugins.UserSession
import roomme.services.AlgoService
import roomme.services.UserDBService

fun Route.routeUserAPI(userService: UserDBService) {
    val algoService = AlgoService.instance!!

    authenticate("auth-session") {
        route("/api/user/") {
            get("next") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!
                val nextShown = algoService.next(user)

                call.respondText(Json.encodeToString(nextShown))
            }

            post("like/{otherId}") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val userId = call.parameters["otherId"]!!
                val otherUser = userService.findUserFromId(userId)

                if (otherUser == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }


            }

            post("dislike/{otherId}") {

            }
        }
    }
}