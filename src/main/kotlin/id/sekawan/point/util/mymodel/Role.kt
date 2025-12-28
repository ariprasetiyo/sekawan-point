package id.sekawan.point.util.mymodel

import java.time.OffsetTime

data class Role(
    var id: String,
    var name: String,
    var description: String? = null,
    var authorization: String? = null,
    var isActive: Boolean,
    var createdAt: OffsetTime,
    var updatedAt: OffsetTime
)