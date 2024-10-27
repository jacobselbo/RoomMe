package roomme.serializables

import kotlinx.serialization.Serializable

@Serializable
data class SockMessage(val id: String, val message: String, val timestamp: Long)
