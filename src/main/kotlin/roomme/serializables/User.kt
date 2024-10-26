package roomme.serializables

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import org.bson.types.ObjectId

data class User(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val id: ObjectId,
    val email: String,
    val password: ByteArray, // Salt-Pepper SHA256 Hashed
    val salt: ByteArray,

    // Bio
    val bio: String,
    val age: Int,
    val major: String,
    val hometown: String,
    val images: Array<String>,

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
