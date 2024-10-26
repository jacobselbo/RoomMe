package roomme.services

import com.mongodb.kotlin.client.coroutine.MongoClient

class MongoService(username: String, password: String) {
    private val uri = "mongodb+srv://$username:$password@development.1swav.mongodb.net/?retryWrites=true&w=majority&appName=Development"
    val client = MongoClient.create(uri)
    val database = client.getDatabase("roomme")

    companion object {
        var instance: MongoService? = null
            get() {
                if (field == null) {
                    error("MongoService is not initialized.")
                }

                return field;
            }
            private set

        fun createInstance(username: String, password: String): MongoService {
            instance = MongoService(username, password)
            return instance as MongoService
        }
    }
}