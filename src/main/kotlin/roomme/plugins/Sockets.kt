package roomme.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableSharedFlow
import org.slf4j.LoggerFactory
import roomme.services.UserDBService
import roomme.services.MessageService

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val messageResponseFlow = MutableSharedFlow<MessageResponse>()
    val sharedFlow = messageResponseFlow.asSharedFlow()

    val userService = UserDBService.instance!!
    val logger = LoggerFactory.getLogger(this.javaClass)

    routing {
        authenticate("auth-session") {
            webSocket("/api/messages") {
                val session = call.sessions.get<UserIdPrincipal>()

                if (session == null) {
                    logger.error("Session not found, should not occur due to authentication")
                    return@webSocket
                }

                val user = userService.getUserFromSession(session)

                MessageService.register(user.id) {
                    return true
                }

                send("Please enter your name")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if (receivedText.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    } else {
                        send(Frame.Text("Hi, $receivedText!"))
                    }
                }
            }
        }
    }
}
