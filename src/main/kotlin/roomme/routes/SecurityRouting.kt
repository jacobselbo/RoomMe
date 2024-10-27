package roomme.routes

import at.favre.lib.crypto.bcrypt.BCrypt.Hasher
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import roomme.Utilities
import roomme.Utilities.Companion.getHTMLFile
import roomme.plugins.UserSession
import roomme.serializables.User
import roomme.services.UserDBService

fun Route.routeSecurity(userService: UserDBService, hashingCost: Int, bCrypt: Hasher) {
    authenticate("auth-form") {
        post("/login") {
            // Create the cookie for the client
            call.principal<UserSession>()?.let {
                call.sessions.set(it)
            }

            call.respondRedirect("/home")
        }
    }

    get("/fakechat") {
        call.respondFile(getHTMLFile("fakechat.html"))
    }

    get("/login") {
        call.respondFile(getHTMLFile("security/login.html"))
    }

    get("/register") {
        call.respondFile(getHTMLFile("security/register.html"))
    }

    post("/register") {
        val formParameters = call.receiveParameters()

        if (!formParameters["email"].toString().matches(Utilities.EMAIL_REGEX) ||
            !formParameters["password"].toString().matches(Utilities.PASSWORD_REGEX) ||
            formParameters["password"].toString() != formParameters["confirmPassword"] ||
            !formParameters["fullname"].toString().matches(Utilities.FULLNAME_REGEX)) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val salt = Utilities.generateRandomSalt()

        val result = userService.users.insertOne(
            User(
                null,
                email = formParameters["email"].toString(),
                password = bCrypt.hash(hashingCost, salt, formParameters["password"]!!.toByteArray()),
                salt = salt,
                fullName = formParameters["fullName"].toString(),
                questionsAnswered = false,
                bio = "",
                age = -1,
                major = "",
                hometown = "",
                images = arrayOf(),

                toMatchWith = arrayOf(),
                currentMatches = arrayOf(),
                liked = arrayOf(),
                disliked = arrayOf(),

                gender = false,
                qSmokeVape = -1,
                qDrink = -1,
                qSleepSchedule = -1,
                qSocial = -1,
                qAloneTime = -1,
                qTemperature = -1,
                qAttractedTo = false,
                qOtherGenders = false
            )
        )

        if (result.insertedId != null) {
            // Create cookie
            call.sessions.set(UserSession(result.insertedId!!.asObjectId().value.toString()))
            call.respondRedirect("/home")
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}