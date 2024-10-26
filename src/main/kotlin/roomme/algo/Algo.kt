package roomme.algo

import io.ktor.server.application.*
import roomme.serializables.User

fun createQuestionEntry(user: User): QuestionEntry {
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
