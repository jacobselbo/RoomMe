package roomme.plugins

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import roomme.routes.api.routeUserAPI
import roomme.routes.frontend.routeFrontEnd
import roomme.routes.routeSecurity
import roomme.services.UserDBService
import java.io.File

fun Application.configureRouting() {
    val hashingCost: Int = environment.config.property("mongodb.hashingCost").getString().toInt()
    val userService = UserDBService.instance!!
    val bCrypt = BCrypt.withDefaults()

    routing {
        get("/") {
            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respondRedirect("/login")
            } else {
                val user = userService.findUserFromSession(session)!!

                if (user.questionsAnswered) {
                    call.respondRedirect("/home")
                } else {
                    call.respondRedirect("/questions")
                }
            }
        }

        routeSecurity(userService, hashingCost, bCrypt)
        routeUserAPI(userService)
        routeFrontEnd()

        // Static Content
        staticFiles("/static", File("resources/static/"))
    }
}
