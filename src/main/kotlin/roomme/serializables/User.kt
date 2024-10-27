package roomme.serializables

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.bson.codecs.kotlinx.ObjectIdSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User @OptIn(ExperimentalSerializationApi::class) constructor(
    @BsonId
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId?,
    val email: String,
    val password: ByteArray, // Salt-Pepper SHA256 Hashed
    val salt: ByteArray,

    val questionsAnswered: Boolean,

    // Bio
    val fullName: String,
    val bio: String,
    val age: Int,
    val major: String,
    val hometown: String,
    val images: Array<String>,

    val currentMatches: Array<@Serializable(with = ObjectIdSerializer::class) ObjectId>, // Active matches
    val toMatchWith: Array<@Serializable(with = ObjectIdSerializer::class) ObjectId>, // Users that liked this user, must be shown next to test

    val liked: Array<@Serializable(with = ObjectIdSerializer::class) ObjectId>, // Roomates that were liked
    val disliked: Array<@Serializable(with = ObjectIdSerializer::class) ObjectId>, // Disliked roomates

    // Questions
    val gender: Boolean,

    val qSmokeVape: Int,
    val qDrink: Int,
    val qSleepSchedule: Int,
    val qSocial: Int,
    val qAloneTime: Int,
    val qTemperature: Int,
    val qAttractedTo: Boolean, // Required Match
    val qOtherGenders: Boolean // Require Match
)
