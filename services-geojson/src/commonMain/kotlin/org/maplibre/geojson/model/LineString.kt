package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.LineStringSerializer
import org.maplibre.geojson.utils.PolylineUtils
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON LineString. LineStrings are build by an array of two or more [Point]s.
 * A LineString must contains at least two points.
 *
 * See [LineString specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.4) for more details.
 *
 * @param points [Point] list, that represents the line
 * @param boundingBox [BoundingBox] of this line
 * @throws IllegalArgumentException if the given list of [Point]s is less than two
 */
@Serializable(with = LineStringSerializer::class)
@SerialName("LineString")
data class LineString(
    /**
     * [Point] list, that represents this line
     */
    val points: List<Point>,

    /**
     * The [BoundingBox] of this line.
     */
    override val boundingBox: BoundingBox? = null
) : Geometry {

    /**
     * Constructor to create a LineString with points.
     *
     * @param points [Point] list, that represents the line
     * @throws IllegalArgumentException if the given list of [Point]s is less than two
     */
    constructor(coordinates: List<Point>) : this(coordinates, null)

    /**
     * Check on initializing, if this LineString is valid.
     * @throws IllegalArgumentException if the given list of [Point]s is less than two
     */
    init {
        require(points.size >= 2) { "LineString must have at least two Points" }
    }

    /**
     * Converts this [LineString] to a Polyline representation with given precision.
     *
     * @param precision precision used to encode polyline
     * @return a polyline String
     */
    fun toPolyline(precision: Int): String {
        return PolylineUtils.encode(points, precision)
    }

    /**
     * Converts this [LineString] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         * Create a new [LineString] from a polyline String.
         *
         * @param polyline a polyline String
         * @param precision precision used to decode polyline
         * @return a new [LineString] representing the given polyline
         * @see PolylineUtils.decode
         */
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int) = fromPolyline(polyline, precision, null)

        /**
         * Create a new [LineString] from a polyline String and [BoundingBox].
         *
         * @param polyline a polyline String
         * @param precision precision used to decode polyline
         * @param bbox [BoundingBox] of this geometry
         * * @return a new [LineString] representing the given polyline
         * @see PolylineUtils.decode
         */
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int, bbox: BoundingBox?): LineString {
            val points = PolylineUtils.decode(polyline, precision)
            return LineString(points, bbox)
        }

        /**
         * Create a new [LineString] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): LineString = json.decodeFromString(jsonString)
    }
}

