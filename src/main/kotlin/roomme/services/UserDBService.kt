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

    suspend fun dislikeUser(user: User, dislikedUser: User): Boolean {
        var success = true

        val addToDisliked = users.updateOne(Filters.eq("_id", user.id),
            Updates.addToSet(User::disliked.name, dislikedUser.id))

        // Remove disliked user from the current user should match with
        if (dislikedUser.id in user.toMatchWith) {
            val removeFromMatchWith = users.updateOne(Filters.eq("_id", user.id),
                Updates.pull(User::toMatchWith.name, dislikedUser.id))

            if (removeFromMatchWith.modifiedCount.toInt() != 1) {
                success = false
                logger.info("Failed to remove ${dislikedUser.id.toString()} from to match with from user ${user.id.toString()}")
            }
        }

        if (addToDisliked.modifiedCount.toInt() != 1) {
            success = false
            logger.info("Failed to modify ${user.id.toString()} to add disliked ${dislikedUser.id.toString()}")
        }

        return success
    }

    suspend fun likeUser(user: User, likedUser: User): Boolean {
        var success = true

        val addToLike = users.updateOne(Filters.eq("_id", user.id),
            Updates.addToSet(User::liked.name, likedUser.id))

        // Remove liked user from the current user should match with
        if (likedUser.id in user.toMatchWith) {
            val removeFromMatchWith = users.updateOne(Filters.eq("_id", user.id),
                Updates.pull(User::toMatchWith.name, likedUser.id))

            if (removeFromMatchWith.modifiedCount.toInt() != 1) {
                success = false
                logger.info("Failed to remove ${likedUser.id.toString()} from to match with from user ${user.id.toString()}")
            }
        }

        // Check if the users like each other
        if (user.id in likedUser.liked) {
            val addToMatchedCU = users.updateOne(Filters.eq("_id", user.id),
                Updates.addToSet(User::currentMatches.name, likedUser.id))
            val addToMatchedLU = users.updateOne(Filters.eq("_id", likedUser.id),
                Updates.addToSet(User::currentMatches.name, user.id))

            if (addToMatchedLU.modifiedCount.toInt() != 1) {
                success = false
                logger.info("Failed to add ${likedUser.id.toString()} to matched array for ${user.id.toString()}")
            }

            if (addToMatchedCU.modifiedCount.toInt() != 1) {
                success = false
                logger.info("Failed to add ${user.id.toString()} to matched array for ${likedUser.id.toString()}")
            }
        } else {
            val addToMatch = users.updateOne(Filters.eq("_id", likedUser.id),
                Updates.addToSet(User::toMatchWith.name, user.id))

            if (addToMatch.modifiedCount.toInt() != 1) {
                success = false
                logger.info("Failed to add ${user.id.toString()} to match be with ${likedUser.id}")
            }
        }

        if (addToLike.modifiedCount.toInt() != 1) {
            success = false
            logger.info("Failed to modify ${user.id.toString()} to add liked ${likedUser.id.toString()}")
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