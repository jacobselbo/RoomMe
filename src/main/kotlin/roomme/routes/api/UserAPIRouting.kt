package roomme.routes.api

import com.mongodb.client.model.Filters
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import roomme.plugins.UserSession
import roomme.services.AlgoService
import roomme.services.UserDBService

@Suppress("ArrayInDataClass")
@Serializable
data class PublicUser(
    val id: String,
    val fullName: String,
    val bio: String,
    val age: Int,
    val major: String,
    val hometown: String,
    val images: Array<String>
)

fun Route.routeUserAPI(userService: UserDBService) {
    val algoService = AlgoService.instance!!
    val userDBService = UserDBService.instance!!

    authenticate("auth-session") {
        route("/api/user/") {
            get("{userId}") {
                val userId: String = call.parameters["userId"]!!
                val user = userDBService.users
                    .find(Filters.eq("_id", userId)).firstOrNull()!!
                val publicUser = PublicUser(
                    id = userId,
                    fullName = user.fullName,
                    bio = user.bio,
                    age = user.age,
                    major = user.major,
                    hometown = user.hometown,
                    images = user.images
                )
                call.respondText(Json.encodeToString(publicUser))
            }

            get("next") {
                val session = call.sessions.get<UserSession>()!!
                val user = userService.findUserFromSession(session)!!
                val nextShown = algoService.next(user)

                call.respondText(Json.encodeToString(nextShown))
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


            }

            post("dislike/{otherId}") {

            }
        }
    }
}