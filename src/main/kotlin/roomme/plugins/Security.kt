package roomme.plugins

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.model.Filters
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.*
import roomme.Utilities
import roomme.services.UserDBService

fun Application.configureSecurity() {
    val hashingCost: Int = environment.config.property("mongodb.hashingCost").getString().toInt()

    val encryptKey = hex(environment.config.property("session.encryptKey").getString())
    val signKey = hex(environment.config.property("session.signKey").getString())

    val bCrypt = BCrypt.withDefaults()
    val userService = UserDBService.instance!!

    install(Sessions) {
        cookie<UserIdPrincipal>("user_session") {
            cookie.path = "/"
            transform(SessionTransportTransformerEncrypt(encryptKey, signKey))
        }
    }

    authentication {
        form(name = "auth-form") {
            userParamName = "email"
            passwordParamName = "password"

            validate { credentials ->
                if (!credentials.name.matches(Utilities.EMAIL_REGEX) ||
                    !credentials.password.matches(Utilities.PASSWORD_REGEX)) { null } else {
                    val user = userService.users.find(Filters.eq("email", credentials.name)).firstOrNull()

                    if (user == null) { null } else {
                        val hash = bCrypt.hash(hashingCost, user.salt, credentials.password.toByteArray())

                        if (hash.contentEquals(user.password)) {
                            UserIdPrincipal(user.id.toString())
                        } else null
                    }
                }
            }
        }
    }
    routing {
        authenticate("myauth1") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
        authenticate("myauth2") {
            get("/protected/route/form") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
}