package roomme.serializables

import kotlinx.serialization.Serializable

@Suppress("ArrayInDataClass")
@Serializable
data class PublicUser(
    val id: String,
    val fullName: String,
    val bio: String,
    val age: Int,
    val major: String,
    val hometown: String,
    val images: Array<String>
) {
    companion object {
        fun createPublicUser(user: User): PublicUser {
            return PublicUser(
                id = user.id!!.toString(),
                fullName = user.fullName,
                bio = user.bio,
                age = user.age,
                major = user.major,
                hometown = user.hometown,
                images = user.images
            )
        }
    }
}