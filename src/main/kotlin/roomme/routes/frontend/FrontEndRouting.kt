package roomme.routes.frontend

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import roomme.Utilities.Companion.getHTMLFile

fun Routing.routeFrontEnd() {
    authenticate("auth-session") {
        get("/messages") {
            call.respondFile(getHTMLFile("frontend/messages.html"))
        }

        get("/home") {
            call.respondFile(getHTMLFile("frontend/home.html"))
        }

        get("/questions") {
            call.respondFile(getHTMLFile("frontend/questions.html"))
        }
    }

    get("/install") {
        call.respondFile(getHTMLFile("frontend/install.html"))
    }
}