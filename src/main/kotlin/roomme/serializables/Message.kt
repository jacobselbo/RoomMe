package roomme.serializables

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation

data class Message(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val sender: String,
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val receiver: String,
    val message: String
)
