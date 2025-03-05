package org.maplibre.geojson.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.BoundingBoxSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON bounding box.
 *
 * See [Bounding Box specification](https://tools.ietf.org/html/rfc7946#section-5) for more details.
 *
 * @param southwest The southwest corner of this bounding box.
 * @param northeast The northeast corner of this bounding box.
 */
@Serializable(with = BoundingBoxSerializer::class)
data class BoundingBox(
    /**
     * The southwest corner of this bounding box.
     */
    val southwest: Point,

    /**
     * The northeast corner of this bounding box.
     */
    val northeast: Point
) {

    /**
     * Constructor to create a bounding box from the given coordinates.
     *
     * @param west The most western longitude of this bounding box.
     * @param south The most southern latitude of this bounding box.
     * @param east The most eastern longitude of this bounding box.
     * @param north The most northern latitude of this bounding box.
     */
    constructor(
        west: Double,
        south: Double,
        east: Double,
        north: Double,
    ) : this(west, south, null, east, north, null)

    /**
     * Constructor to create a bounding box from the given coordinates.
     *
     * @param west The most western longitude of this bounding box.
     * @param south The most southern latitude of this bounding box.
     * @param southwestAltitude The altitude at the southwest corner of this bounding box.
     * @param east The most eastern longitude of this bounding box.
     * @param north The most northern latitude of this bounding box.
     * @param northEastAltitude The altitude at the northeast corner of this bounding box.
     */
    constructor(
        west: Double,
        south: Double,
        southwestAltitude: Double?,
        east: Double,
        north: Double,
        northEastAltitude: Double?
    ) : this(
        Point(west, south, southwestAltitude),
        Point(east, north, northEastAltitude)
    )

    /**
     * Convenience field to get the most western longitude of this bounding box.
     */
    val west: Double
        get() = southwest.longitude

    /**
     * Convenience field to get the most southern latitude of this bounding box.
     */
    val south: Double
        get() = southwest.latitude

    /**
     * Convenience field to get the most eastern longitude of this bounding box.
     */
    val east: Double
        get() = northeast.longitude

    /**
     * Convenience field to get the most northern latitude of this bounding box.
     */
    val north: Double
        get() = northeast.latitude

    /**
     * Converts this [BoundingBox] to its GeoJSON representation, as [String].
     *
     * @return a [String] that contains JSON
     */
    fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [BoundingBox] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON [String]
         */
        @JvmStatic
        fun fromJson(jsonString: String): BoundingBox = json.decodeFromString(jsonString)
    }
}
