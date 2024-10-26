package roomme.algo

import kotlin.math.sqrt

class QuestionEntry(vector: Array<Double>) {
    private var vector: Array<Double> = Array(Algo.getEntryNumber()) {
        (vector[it] - Algo.getLowerScale()) / (Algo.getUpperScale() - Algo.getLowerScale())
    }

    fun compareTo(other: QuestionEntry): Double {
        var dot = 0.0
        for (i in vector.indices) {
            dot += (other.vector[i] - vector[i]) * (other.vector[i] - vector[i])
        }
        dot /= Algo.getEntryNumber()
        dot = sqrt(dot)
        return 1 - dot
    }
}
