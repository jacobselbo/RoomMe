package roomme.routes.api

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.conversions.Bson
import roomme.plugins.UserSession
import roomme.serializables.PublicUser
import roomme.serializables.User
import roomme.services.AlgoService
import roomme.services.MessageDBService
import roomme.services.UserDBService

fun Route.routeUserAPI(userService: UserDBService) {
    val WHITELIST_USER_FIELDS = setOf(
        User::fullName.name,
        User::bio.name,
        User::age.name,
        User::major.name,
        User::hometown.name,
        User::images.name,
        User::gender.name,
        User::qSmokeVape.name,
        User::qDrink.name,
        User::qSleepSchedule.name,
        User::qSocial.name,
        User::qAloneTime.name,
        User::qTemperature.name,
        User::qAttractedTo.name,
        User::qOtherGenders.name
    )

    val algoService = AlgoService.instance!!
    val userDBService = UserDBService.instance!!
    val messageDbService = MessageDBService.instance!!

    authenticate("auth-session") {
        route("/api/user/") {
            get("self") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                call.respondText(Json.encodeToString(PublicUser.createPublicUser(user)))
            }

            get("{userId}") {
                val userId: String = call.parameters["userId"]!!
                val user = userDBService.users
                    .find(Filters.eq("_id", userId)).firstOrNull()!!

                call.respondText(Json.encodeToString(PublicUser.createPublicUser(user)))
            }

            get("next") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!
                val nextShown = algoService.next(user)

                call.respondText(Json.encodeToString(PublicUser.createPublicUser(nextShown)))
            }

            post("like/{otherId}") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val userId = call.parameters["otherId"]!!
                val otherUser = userService.findUserFromId(userId)

                if (otherUser == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val success = userService.likeUser(user, otherUser)

                if (success)
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.InternalServerError)
            }

            post("dislike/{otherId}") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val userId = call.parameters["otherId"]!!
                val otherUser = userService.findUserFromId(userId)

                if (otherUser == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val success = userService.dislikeUser(user, otherUser)

                if (success)
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.InternalServerError)
            }

            get("messages") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                call.respondText(Json.encodeToString(messageDbService.getMessages(user)))
            }

            post("update") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val data = call.receive<HashMap<String, String>>()
                val updates = mutableListOf<Bson>()

                for ((key, value) in data) {
                    if (key !in WHITELIST_USER_FIELDS)
                        continue
                    // TODO: Remember this can be XSS, and we really need to sanitize input
                    updates.add(Updates.set(key, value))
                }

                val result = userDBService.users.updateOne(
                    Filters.eq("_id", user.id),
                    Updates.combine(updates)
                )

                if (result.modifiedCount.toInt() < updates.size) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}