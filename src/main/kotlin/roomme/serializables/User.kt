package roomme.serializables

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation

data class User(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val id: String,
    val email: String,
    val password: String, // Salt-Pepper SHA256 Hash

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
