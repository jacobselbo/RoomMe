package roomme.services

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import roomme.serializables.Message
import roomme.serializables.User

class MessageDBService private constructor(mongoService: MongoService) {
    val messages = mongoService.database.getCollection<Message>("messages")

    suspend fun getMessages(user: User): Array<Message> {
        return messages.find(
            Filters.or(
                Filters.eq(Message::sender.name, user.id),
                Filters.eq(Message::receiver.name, user.id),
            )).toList().toTypedArray()
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

        // TODO: messageSent implement to add message to database
        fun messageSent(sender: ObjectId, receiver: ObjectId, message: String, timestamp: Long) {

        }
    }
}