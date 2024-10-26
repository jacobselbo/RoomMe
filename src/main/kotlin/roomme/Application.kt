package roomme

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import roomme.algo.configureAlgo
import roomme.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureAlgo()
    // configureSecurity()
    configureSerialization()
    configureSingletons()
    configureSockets()
    configureRouting()
}
