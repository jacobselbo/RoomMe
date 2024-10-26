package roomme.services

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.first
import org.bson.types.ObjectId
import roomme.algo.QuestionEntry
import roomme.serializables.User

class AlgoService private constructor(
    private val lowerScale: Double,
    private val upperScale: Double,
    private val entryNumber: Int
) {
    companion object {
        private var instance: AlgoService? = null

        private fun getInstance(): AlgoService {
            return instance as AlgoService
        }

        fun createInstance(lowerScale: Double, upperScale: Double, entryNumber: Int): AlgoService {
            instance = AlgoService(lowerScale, upperScale, entryNumber)
            return instance as AlgoService
        }

        fun getLowerScale(): Double {
            return getInstance().lowerScale
        }

        fun getUpperScale(): Double {
            return getInstance().upperScale
        }

        fun getEntryNumber(): Int {
            return getInstance().entryNumber
        }
    }

    val userDBService = UserDBService.instance!!

    private fun createQuestionEntry(user: User): QuestionEntry {
        return QuestionEntry(
            user.gender,
            user.qAttractedTo,
            arrayOf(
                user.qDrink,
                user.qSocial,
                user.qTemperature,
                user.qAloneTime,
                user.qSmokeVape,
                user.qSleepSchedule
            )
        )
    }

    private fun matchScore(user1: User, user2: User): Double {
        return createQuestionEntry(user1).matchScore(createQuestionEntry(user2))
    }

    suspend fun next(user: User): User {
        if (user.toMatchWith.isNotEmpty()) {
            val id = user.toMatchWith[0]

            return userDBService.users.find(Filters.eq("_id", id)).first()
        }

        val usersSwipedOn: ArrayList<ObjectId> = ArrayList()

        usersSwipedOn.addAll(user.liked)
        usersSwipedOn.addAll(user.disliked)

        val users: ArrayList<User> = ArrayList()

        userDBService.users.find(Filters.nin("_id", usersSwipedOn)).collect {
            u -> users.add(u)
        }

        users.sortByDescending {
            u -> matchScore(user, u)
        }

        return users[0]
    }
}