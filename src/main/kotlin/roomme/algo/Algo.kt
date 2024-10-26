package roomme.algo

class Algo private constructor(
    private val lowerScale: Double,
    private val upperScale: Double,
    private val entryNumber: Int,
    ) {

    companion object {
        private var instance: Algo? = null

        fun getInstance(): Algo {
            return instance as Algo
        }

        fun createInstance(lowerScale: Double, upperScale: Double, entryNumber: Int): Algo {
            instance = Algo(lowerScale, upperScale, entryNumber)
            return instance as Algo
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

}