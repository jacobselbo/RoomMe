package roomme.services

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import roomme.serializables.DateLongSerializer
import roomme.serializables.Message
import roomme.serializables.User
import java.util.*

class MessageDBService private constructor(mongoService: MongoService) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val messages = mongoService.database.getCollection<Message>("messages")

    suspend fun getMessages(user: User): Array<Message> {
        return messages.find(
            Filters.or(
                Filters.eq(Message::sender.name, user.id),
                Filters.eq(Message::receiver.name, user.id),
            )).toList().toTypedArray()
    }

    suspend fun messageSent(sender: ObjectId, receiver: ObjectId, message: String, timestamp: Long) {
        val update = messages.insertOne(Message(sender, receiver, message, Date(timestamp)))

        if (!update.wasAcknowledged()) {
            logger.warn("Failed to insert '$message' with sender '$sender' to '$receiver'")
        }
    }

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
    }
}