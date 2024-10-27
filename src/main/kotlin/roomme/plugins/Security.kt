package roomme.plugins

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.model.Filters
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.*
import roomme.Utilities
import roomme.serializables.User
import roomme.services.UserDBService

@Serializable
data class UserSession(val id: String)

fun Application.configureSecurity() {
    val hashingCost: Int = environment.config.property("mongodb.hashingCost").getString().toInt()

    val encryptKey = hex(environment.config.property("session.encryptKey").getString())
    val signKey = hex(environment.config.property("session.signKey").getString())

    val bCrypt = BCrypt.withDefaults()
    val userService = UserDBService.instance!!

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            transform(SessionTransportTransformerEncrypt(encryptKey, signKey))
        }
    }

    authentication {
        form("auth-form") {
            userParamName = "email"
            passwordParamName = "password"

            validate { credentials ->
                if (!credentials.name.matches(Utilities.EMAIL_REGEX) ||
                    !credentials.password.matches(Utilities.PASSWORD_REGEX)) { null } else {
                    val user = userService.users.find(Filters.eq(User::email.name, credentials.name)).firstOrNull()

                    if (user == null) { null } else {
                        val hash = bCrypt.hash(hashingCost, user.salt, credentials.password.toByteArray())

                        if (hash.contentEquals(user.password)) {
                            UserSession(user.id!!.toString())
                        } else null
                    }
                }
            }
            challenge {
                call.respondRedirect("/login#failure")
            }
        }
        session("auth-session") {
            validate { session ->
                if (userService.findUserFromSession(session) != null)
                    session
                else null
            }

            challenge {
                call.respondRedirect("/login")
            }
        }
    }
}