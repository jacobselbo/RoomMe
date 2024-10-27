package roomme.serializables

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import org.bson.codecs.kotlinx.BsonDecoder
import org.bson.codecs.kotlinx.ObjectIdSerializer
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

object DateLongSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Date = Date(decoder.decodeLong())
}

object ObjectIdStringSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "ObjectIdStringSerializer", PrimitiveKind.STRING
    )
    override fun serialize(encoder: Encoder, value: ObjectId) {
        return when (encoder) {
            is JsonEncoder -> encoder.encodeString(value.toString())
            else -> encoder.encodeString(value.toString())
        }
    }
    override fun deserialize(decoder: Decoder): ObjectId {
        return when (decoder) {
            is BsonDecoder -> decoder.decodeBsonValue().asObjectId().value
            else -> throw UnsupportedOperationException()
        }
    }
}

@Serializable
data class Message @OptIn(ExperimentalSerializationApi::class) constructor(
    @Serializable(with = ObjectIdSerializer::class)
    val sender: ObjectId,
    @Serializable(with = ObjectIdSerializer::class)
    val receiver: ObjectId,
    val message: String,
    @Serializable(with = DateLongSerializer::class)
    val timestamp: Date
)

@Serializable
data class PublicMessage(
    @Serializable(with = ObjectIdStringSerializer::class)
    val sender: ObjectId,
    @Serializable(with = ObjectIdStringSerializer::class)
    val receiver: ObjectId,
    val message: String,
    @Serializable(with = DateLongSerializer::class)
    val timestamp: Date
)