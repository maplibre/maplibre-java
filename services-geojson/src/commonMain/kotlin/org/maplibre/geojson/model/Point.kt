package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON Point. Points are built by required [latitude] and [longitude].
 * Additionally, an optional [altitude] and [boundingBox] can be provided.
 *
 * See [Point specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.2) and
 * [Position specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.1) for more details.
 *
 * @param longitude longitude coordinate of this point
 * @param latitude latitude coordinate of this point
 * @param altitude altitude or elevation of this point
 * @param boundingBox [BoundingBox] of this point
 */
@Serializable(with = PointSerializer::class)
@SerialName("Point")
data class Point(
    /**
     * Longitude coordinate of this point.
     */
    val longitude: Double,

    /**
     * Latitude coordinate of this point.
     */
    val latitude: Double,

    /**
     * Altitude or elevation of this point.
     */
    val altitude: Double? = null,

    /**
     * The [BoundingBox] of this point.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a Point with longitude and latitude.
     *
     * @param longitude longitude coordinate of this point
     * @param latitude latitude coordinate of this point
     */
    constructor(
        longitude: Double,
        latitude: Double,
    ) : this(longitude, latitude, null, null)

    /**
     * Constructor to create a Point with longitude, latitude and altitude.
     *
     * @param longitude longitude coordinate of this point
     * @param latitude latitude coordinate of this point
     * @param altitude altitude or elevation of this point
     */
    constructor(
        longitude: Double,
        latitude: Double,
        altitude: Double?,
    ) : this(longitude, latitude, altitude, null)

    /**
     * Converts this [Point] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         * Create a new [Point] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): Point = json.decodeFromString(jsonString)
    }
}
