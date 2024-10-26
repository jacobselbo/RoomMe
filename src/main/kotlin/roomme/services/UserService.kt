package roomme.services

import roomme.serializables.User

class UserService private constructor(mongoService: MongoService) {
    val users = mongoService.database.getCollection<User>("users")


    companion object {
        private var instance: UserService? = null

        fun getInstance(): UserService {
            if (instance == null) {
                error("UserService is not initialized.")
            }

            return instance as UserService
        }

        fun createInstance(mongoService: MongoService): UserService {
            instance = UserService(mongoService)
            return instance as UserService
        }
    }
}