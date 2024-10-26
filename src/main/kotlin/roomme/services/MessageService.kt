package roomme.services

import org.bson.types.ObjectId

interface MessageHandler {
    fun handle(sender: ObjectId, message: String): Boolean
}

class MessageService {
    private val handlers: HashMap<ObjectId, MessageHandler> = HashMap()

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

        fun register(id: ObjectId, handler: MessageHandler) {
            instance!!.handlers[id] = handler
        }

        fun sendMessage(sender: ObjectId, receiver: ObjectId, message: String) {
            instance!!.handlers[receiver]?.handle(sender, message)
            MessageDBService.messageSent(sender, receiver, message)
        }

        fun close(id: ObjectId) {
            instance!!.handlers.remove(id)
        }
    }
}