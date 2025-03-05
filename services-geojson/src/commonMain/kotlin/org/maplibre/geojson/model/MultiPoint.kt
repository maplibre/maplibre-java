package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiPointSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = MultiPointSerializer::class)
@SerialName("MultiPoint")
data class MultiPoint(
    /**
     *
     */
    val points: List<Point>,

    /**
     *
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     *
     */
    constructor(coordinates: List<Point>) : this(coordinates, null)

    /**
     *
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPoint = json.decodeFromString(jsonString)
    }
}
