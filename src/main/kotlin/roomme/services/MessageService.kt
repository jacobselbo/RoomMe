package roomme.services

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
    }
}