package roomme.services

import roomme.serializables.Message

class MessageService private constructor(mongoService: MongoService) {
    val messages = mongoService.database.getCollection<Message>("messages")

    companion object {
        private var instance: MessageService? = null

        fun getInstance(): MessageService {
            if (instance == null) {
                error("MessageService is not initialized.")
            }

            return instance as MessageService
        }

        fun createInstance(mongoService: MongoService): MessageService {
            instance = MessageService(mongoService)
            return instance as MessageService
        }
    }
}