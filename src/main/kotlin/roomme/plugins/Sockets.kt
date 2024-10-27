package roomme.plugins

import com.mongodb.client.model.Filters
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import roomme.services.MessageHandler
import roomme.services.UserDBService
import roomme.services.MessageService

@Serializable
data class SockMessage(val id: String, val message: String, val timestamp: Long)

@Serializable
data class InMessage(val id: String, val message: String)

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val userService = UserDBService.instance!!
    val logger = LoggerFactory.getLogger(this.javaClass)
    logger.info("HELLO BITCHO!")

    routing {
        authenticate("auth-session") {
            webSocket("/api/messages") {
                val session = call.sessions.get<UserSession>()

                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                    return@webSocket
                }

                val user = userService.findUserFromSession(session)!!

                try {
                    MessageService.register(user.id!!, MessageHandler(
                        user.id,
                        this
                    ))
                    incoming.consumeEach { frame ->
                        if(frame is Frame.Text) {
                            val txt = frame.readText()
                            val msg: InMessage = Json.decodeFromString(txt)
                            MessageService.sendMessage(
                                user.id,
                                ObjectId(msg.id),
                                msg.message
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    MessageService.close(user.id!!)
                }
            }
        }
    }
}
