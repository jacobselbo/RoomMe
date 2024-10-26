package roomme.plugins

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import roomme.routes.api.routeUserAPI
import roomme.routes.routeSecurity
import roomme.services.UserDBService

fun Application.configureRouting() {
    val hashingCost: Int = environment.config.property("mongodb.hashingCost").getString().toInt()
    val userService = UserDBService.instance!!
    val bCrypt = BCrypt.withDefaults()

    routing {
        authenticate("auth-session") {
            get("/") {
                val session = call.sessions.get<UserSession>()
                val user = userService.getUserFromSession(session!!)!!

                if (user.questionsAnswered) {
                    call.respondRedirect("/home")
                } else {
                    call.respondRedirect("/questions")
                }
            }
        }

        get("/") {
            call.respondRedirect("/login")
        }

        routeSecurity(hashingCost, bCrypt)
        routeUserAPI()

        // Static Content
        staticResources("/resources/static", "resources/static")
    }
}
