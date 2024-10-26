package roomme.serializables

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import org.bson.types.ObjectId

data class Message(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val sender: ObjectId,
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val receiver: ObjectId,
    val message: String
)
