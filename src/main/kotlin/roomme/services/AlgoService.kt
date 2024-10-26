package roomme.services

class AlgoSingleton private constructor(
    private val lowerScale: Double,
    private val upperScale: Double,
    private val entryNumber: Int
) {

    companion object {
        private var instance: AlgoSingleton? = null

        fun getInstance(): AlgoSingleton {
            return instance as AlgoSingleton
        }

        fun createInstance(lowerScale: Double, upperScale: Double, entryNumber: Int): AlgoSingleton {
            instance = AlgoSingleton(lowerScale, upperScale, entryNumber)
            return instance as AlgoSingleton
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