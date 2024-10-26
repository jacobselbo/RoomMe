package roomme.services

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import roomme.plugins.UserSession
import roomme.serializables.User

class UserDBService private constructor(mongoService: MongoService) {
    val users = mongoService.database.getCollection<User>("users")

    suspend fun getUserFromSession(session: UserSession): User? {
        return users.find(Filters.eq("_id", ObjectId(session.id))).firstOrNull()
    }

    companion object {
        var instance: UserDBService? = null
            get() {
                if (field == null) {
                    error("MessageService is not initialized.")
                }

                return field
            }
            private set

        fun createInstance(mongoService: MongoService): UserDBService {
            instance = UserDBService(mongoService)
            return instance as UserDBService
        }
    }
}