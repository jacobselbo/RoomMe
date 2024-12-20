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
import org.bson.types.ObjectId
import roomme.plugins.UserSession
import roomme.serializables.PublicMessage
import roomme.serializables.PublicUser
import roomme.serializables.User
import roomme.services.AlgoService
import roomme.services.MessageDBService
import roomme.services.UserDBService
import java.lang.Boolean.parseBoolean
import java.lang.Integer.parseInt

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
        User::qOtherGenders.name,
        User::questionsAnswered.name
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

            get("get/{userId}") {
                val userId: String = call.parameters["userId"]!!
                val user = userDBService.users
                    .find(Filters.eq("_id", ObjectId(userId))).firstOrNull()!!

                call.respondText(Json.encodeToString(PublicUser.createPublicUser(user)))
            }

            get("next") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!
                val nextShown = algoService.next(user)

                call.respondText(Json.encodeToString(PublicUser.createPublicUser(nextShown)))
            }

            get("like/{otherId}") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val userId = call.parameters["otherId"]!!
                val otherUser = userService.findUserFromId(userId)

                if (otherUser == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val success = userService.likeUser(user, otherUser)

                if (success)
                    call.respond(HttpStatusCode.OK)
                else
                    call.respond(HttpStatusCode.InternalServerError)
            }

            get("dislike/{otherId}") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!

                val userId = call.parameters["otherId"]!!
                val otherUser = userService.findUserFromId(userId)

                if (otherUser == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
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

                val data = messageDbService.getMessages(user)
                val sendData = Array(data.size) { i ->
                    val message = data[i]

                    PublicMessage(
                        sender = message.sender,
                        receiver = message.receiver,
                        message = message.message,
                        timestamp = message.timestamp
                    )
                }

                call.respondText(Json.encodeToString(sendData))
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

                    val setValue: Any = when (key) {
                        User::age.name, User::qSmokeVape.name, User::qDrink.name, User::qSleepSchedule.name, User::qSocial.name, User::qAloneTime.name, User::qTemperature.name -> parseInt(value)
                        User::qOtherGenders.name, User::gender.name, User::questionsAnswered.name -> parseBoolean(value)
                        else -> value
                    }

                    updates.add(Updates.set(key, setValue))
                }

                val result = userDBService.users.updateMany(
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