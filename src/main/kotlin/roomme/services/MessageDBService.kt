package roomme.services

import org.bson.types.ObjectId
import roomme.serializables.Message

class MessageDBService private constructor(mongoService: MongoService) {
    val messages = mongoService.database.getCollection<Message>("messages")

    companion object {
        var instance: MessageDBService? = null
            get() {
                if (field == null) {
                    error("MessageDBService is not initialized.")
                }

                return field
            }
            private set

        fun createInstance(mongoService: MongoService): MessageDBService {
            instance = MessageDBService(mongoService)
            return instance as MessageDBService
        }

        // TODO: messageSent implement to add message to database
        fun messageSent(sender: ObjectId, receiver: ObjectId, message: String, timestamp: Long) {

        }
    }
}