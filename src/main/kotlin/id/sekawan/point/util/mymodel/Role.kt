package id.sekawan.point.util.mymodel

import io.vertx.core.json.JsonObject
import java.time.OffsetDateTime

data class Role(
    var id: String,
    var name: String,
    var description: String? = null,
    var authorization: JsonObject? = null,
    var isActive: Boolean,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime
)