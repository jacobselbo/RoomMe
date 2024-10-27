package roomme.serializables

import kotlinx.serialization.Serializable
import org.bson.BsonTimestamp
import org.bson.BsonType
import org.bson.codecs.kotlinx.ObjectIdSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import org.bson.types.ObjectId

@Serializable
data class Message(
    @BsonId
    @Serializable(with = ObjectIdSerializer::class)
    val sender: ObjectId,
    @BsonId
    @Serializable(with = ObjectIdSerializer::class)
    val receiver: ObjectId,
    val message: String,
    val timestamp: BsonTimestamp
)
