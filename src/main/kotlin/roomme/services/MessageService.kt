package roomme.services

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import roomme.plugins.SockMessage

data class MessageHandler(
    val id: ObjectId,
    val sock: WebSocketSession
)

class MessageService {
    private val handlers = hashMapOf<ObjectId, MessageHandler>()

    companion object {
        var instance: MessageService? = null
            get() {
                if (field == null) {
                    error("MessageService is not initialized.")
                }

                return field
            }
            private set

        fun createInstance(): MessageService {
            instance = MessageService()
            return instance as MessageService
        }

        suspend fun register(id: ObjectId, handler: MessageHandler) {
            if (id in instance!!.handlers)
                close(id)
            instance!!.handlers[id] = handler
        }

        suspend fun sendMessage(sender: ObjectId, receiver: ObjectId, message: String) {
            val timestamp = System.currentTimeMillis()
            val msg = SockMessage(sender.toString(), message, timestamp)
            instance!!.handlers[receiver]?.sock?.send(
                Frame.Text(Json.encodeToString(msg))
            )
            MessageDBService.messageSent(sender, receiver, message, timestamp)
        }

        suspend fun close(id: ObjectId) {
            instance!!.handlers[id]?.sock?.close()
            if (id in instance!!.handlers)
                instance!!.handlers.remove(id)
        }
    }
}