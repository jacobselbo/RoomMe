package roomme.plugins

import io.ktor.server.application.*
import roomme.services.MongoService
import roomme.services.UserService

fun Application.configureSingletons() {
    val mongoUsername = environment.config.property("mongodb.username").getString()
    val mongoPassword = environment.config.property("mongodb.password").getString()

    // Set up database
    val mongoService = MongoService.createInstance(mongoUsername, mongoPassword)
    UserService.createInstance(mongoService)
}