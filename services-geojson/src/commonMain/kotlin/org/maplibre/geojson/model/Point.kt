package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = PointSerializer::class)
@SerialName("Point")
data class Point(
    /**
     *
     */
    val longitude: Double,

    /**
     *
     */
    val latitude: Double,

    /**
     *
     */
    val altitude: Double? = null,

    /**
     *
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     *
     */
    constructor(
        longitude: Double,
        latitude: Double,
    ) : this(longitude, latitude, null, null)

    /**
     *
     */
    constructor(
        longitude: Double,
        latitude: Double,
        altitude: Double?,
    ) : this(longitude, latitude, altitude, null)

    /**
     *
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): Point = json.decodeFromString(jsonString)
    }
}
