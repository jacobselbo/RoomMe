package roomme.services

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

}