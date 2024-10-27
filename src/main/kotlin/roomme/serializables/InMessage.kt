package roomme.serializables

import kotlinx.serialization.Serializable

@Serializable
data class InMessage(val id: String, val message: String)
