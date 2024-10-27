package roomme.algo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import roomme.services.AlgoService
import kotlin.math.max
import kotlin.math.sqrt

class QuestionEntry(
    private val gender: Boolean,
    private val attract: Boolean,
    vector: Array<Int>
) {
    private val algoService = AlgoService.instance!!
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private var vector: Array<Double> = Array(algoService.entryNumber) {
        (vector[it] - algoService.lowerScale) / (algoService.upperScale - algoService.lowerScale)
    }

    init {
        if (vector.size != algoService.entryNumber)
            logger.error("Vector size should be %d, but was %d".format(
                algoService.entryNumber, vector.size
            ))
    }

    fun matchScore(other: QuestionEntry): Double {
        if (other.gender == attract || gender == other.attract)
            return 0.0

        var dot = 0.0
        var entries = 0
        for (i in vector.indices) {
            if (other.vector[i] == -1.0)
                continue
            if (vector[i] == -1.0)
                continue

            entries++
            dot += (other.vector[i] - vector[i]) * (other.vector[i] - vector[i])
        }
        dot /= max(1, entries)
        dot = sqrt(dot)
        return 1 - dot
    }
}
