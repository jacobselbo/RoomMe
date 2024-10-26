package roomme.services

import com.mongodb.client.model.Filters
import io.ktor.server.auth.*
import kotlinx.coroutines.flow.firstOrNull
import roomme.serializables.User

class UserDBService private constructor(mongoService: MongoService) {
    val users = mongoService.database.getCollection<User>("users")

    suspend fun getUserFromSession(session: UserIdPrincipal): User? {
        return users.find(Filters.eq("_id", session.name)).firstOrNull()
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