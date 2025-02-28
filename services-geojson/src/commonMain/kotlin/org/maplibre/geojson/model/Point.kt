package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PointSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * A point represents a single geographic position and is one of the seven Geometries found in the
 * GeoJson spec.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * Coordinates are in x, y order (easting, northing for projected coordinates), longitude, and
 * latitude for geographic coordinates), precisely in that order and using double values. Altitude
 * or elevation MAY be included as an optional third parameter while creating this object.
 *
 *
 * The size of a GeoJson text in bytes is a major interoperability consideration, and precision of
 * coordinate values has a large impact on the size of texts when serialized. For geographic
 * coordinates with units of degrees, 6 decimal places (a default common in, e.g., sprintf) amounts
 * to about 10 centimeters, a precision well within that of current GPS systems. Implementations
 * should consider the cost of using a greater precision than necessary.
 *
 *
 * Furthermore, pertaining to altitude, the WGS 84 datum is a relatively coarse approximation of the
 * geoid, with the height varying by up to 5 m (but generally between 2 and 3 meters) higher or
 * lower relative to a surface parallel to Earth's mean sea level.
 *
 *
 * A sample GeoJson Point's provided below (in its serialized state).
 * ```json
 * {
 *   "type": "Point",
 *   "coordinates": [100.0, 0.0]
 * }
 * ```
 *
 * //TODO
 * @param
 * @param
 * @since 1.0.0
 */
@Serializable(with = PointSerializer::class)
@SerialName("Point")
data class Point(
    /**
     * Double value representing the x or easting position of this point.
     */
    val longitude: Double,

    /**
     * Double value representing the y or northing position of this point.
     */
    val latitude: Double,

    /**
     * Double value representing altitude or elevation of this point.
     */
    val altitude: Double? = null,

    /**
     * Bounding box for this point.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    //TODO
    /**
     * Create a new instance of this class defining a longitude and latitude value in that respective
     * order. While no limit is placed on decimal precision, for performance reasons
     * when serializing and deserializing it is suggested to limit decimal precision to within 6
     * decimal places. An optional altitude value can be passed in and can vary between negative
     * infinity and positive infinity.
     *
     * @param longitude a double value representing the x position of this point
     * @param latitude  a double value representing the y position of this point
     * elevation or altitude
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 7.0.0
     */
    constructor(
        longitude: Double,
        latitude: Double,
    ) : this(longitude, latitude, null, null)

    //TODO
    /**
     * Create a new instance of this class defining a longitude and latitude value in that respective
     * order. While no limit is placed on decimal precision, for performance reasons
     * when serializing and deserializing it is suggested to limit decimal precision to within 6
     * decimal places. An optional altitude value can be passed in and can vary between negative
     * infinity and positive infinity.
     *
     * @param longitude a double value representing the x position of this point
     * @param latitude  a double value representing the y position of this point
     * @param altitude  a double value which can be negative or positive infinity representing either
     * elevation or altitude
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 7.0.0
     */
    constructor(
        longitude: Double,
        latitude: Double,
        altitude: Double?,
    ) : this(longitude, latitude, altitude, null)

    /**
     * Create GeoJSON representation of this [Point] geometry.
     *
     * @return a string that contains GeoJSON
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         * Create a new [Point] from a GeoJSON String
         *
         * @param jsonString a GeoJSON string that represents a point
         * @return a new instance Point instance
         */
        @JvmStatic
        fun fromJson(jsonString: String): Point = json.decodeFromString(jsonString)
    }
}
