package roomme.algo

import kotlin.math.max
import kotlin.math.sqrt

class QuestionEntry(vector: Array<Double>) {
    private var vector: Array<Double> = Array(AlgoSingleton.getEntryNumber()) {
        (vector[it] - AlgoSingleton.getLowerScale()) / (AlgoSingleton.getUpperScale() - AlgoSingleton.getLowerScale())
    }

    init {
        assert(vector.size == AlgoSingleton.getEntryNumber()) {
            "Vector size should be %d, but was %d".format(
                AlgoSingleton.getEntryNumber(), vector.size
            )
        }
    }

    fun compareTo(other: QuestionEntry): Double {
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
