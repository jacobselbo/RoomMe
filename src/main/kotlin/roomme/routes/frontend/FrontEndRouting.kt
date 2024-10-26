package roomme.routes.frontend

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.routeFrontEnd() {
    authenticate("auth-session") {
        get("/questions") {
            call.respondText("test")
        }
    }
}