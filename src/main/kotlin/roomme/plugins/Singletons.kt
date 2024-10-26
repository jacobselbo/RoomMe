package roomme.plugins

import io.ktor.server.application.*
import roomme.services.*

fun Application.configureSingletons() {
    val mongoUsername = environment.config.property("mongodb.username").getString()
    val mongoPassword = environment.config.property("mongodb.password").getString()

    // Set up database
    val mongoService = MongoService.createInstance(mongoUsername, mongoPassword)
    UserDBService.createInstance(mongoService)
    MessageDBService.createInstance(mongoService)

    // Set up Message Event Handler
    //MessageService.createInstance()

    // Configure Algo Service
    //AlgoService.createInstance(0.0, 10.0, 6)
}