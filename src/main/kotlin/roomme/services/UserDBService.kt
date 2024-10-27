package roomme.services

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import roomme.plugins.UserSession
import roomme.serializables.User

class UserDBService private constructor(mongoService: MongoService) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    val users = mongoService.database.getCollection<User>("users")

    suspend fun findUserFromId(id: String): User? {
        return users.find(Filters.eq("_id", ObjectId(id))).firstOrNull()
    }

    suspend fun findUserFromSession(session: UserSession): User? {
        return findUserFromId(session.id)
    }

    suspend fun likeUser(user: User, likedUser: User): Boolean {
        val addToLike = users.updateOne(Filters.eq("_id", user.id),
            Updates.addToSet(User::liked.name, likedUser.id))
        val addToMatch = users.updateOne(Filters.eq("_id", likedUser.id),
            Updates.addToSet(User::toMatchWith.name, user.id))

        var success = true

        if (addToLike.modifiedCount.toInt() != 1) {
            success = false
            logger.info("Failed to modify ${user.id.toString()} to add liked ${likedUser.id.toString()}")
        }

        if (addToMatch.modifiedCount.toInt() != 1) {
            success = false
            logger.info("Failed to add ${user.id.toString()} to match be with ${likedUser.id}")
        }

        return success
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