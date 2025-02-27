package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.exception.GeoJsonException
import org.maplibre.geojson.serializer.PointDoubleArraySerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * This class represents a GeoJson Polygon which may or may not include polygon holes.
 *
 *
 * To specify a constraint specific to Polygons, it is useful to introduce the concept of a linear
 * ring:
 *
 *  * A linear ring is a closed LineString with four or more coordinates.
 *  * The first and last coordinates are equivalent, and they MUST contain identical values; their
 * representation SHOULD also be identical.
 *  * A linear ring is the boundary of a surface or the boundary of a hole in a surface.
 *  * A linear ring MUST follow the right-hand rule with respect to the area it bounds, i.e.,
 * exterior rings are counterclockwise, and holes are clockwise.
 *
 * Note that most of the rules listed above are checked when a Polygon instance is created (the
 * exception being the last rule). If one of the rules is broken, a [RuntimeException] will
 * occur.
 *
 *
 * Though a linear ring is not explicitly represented as a GeoJson geometry TYPE, it leads to a
 * canonical formulation of the Polygon geometry TYPE. When initializing a new instance of this
 * class, a LineString for the outer and optionally an inner are checked to ensure a valid linear
 * ring.
 *
 *
 * An example of a serialized polygon with no holes is given below:
 * ```json
 * {
 *   "TYPE": "Polygon",
 *   "coordinates": [
 *     [[100.0, 0.0],
 *     [101.0, 0.0],
 *     [101.0, 1.0],
 *     [100.0, 1.0],
 *     [100.0, 0.0]]
 *   ]
 * }
 * ```
 *
 * //TODO
 * @param coordinates a list of a list of points which represent the polygon geometry
 * @param bbox        optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("Polygon")
data class Polygon(
    //TODO
    val outerLineString: LineString,
    //TODO
    val holeLineStrings: List<LineString>,
    //TODO
    override val bbox: BoundingBox?,
) : Geometry {

    //TODO
    /**
     * Create a new instance of this class by passing in an outer [LineString] and optionally
     * one or more inner LineStrings. Each of these LineStrings should follow the linear ring rules.
     *
     *
     * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
     * be thrown.
     *
     * @param outer a LineString which defines the outer perimeter of the polygon
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 3.0.0
     */
    constructor(outer: LineString) : this(outer, emptyList(), null)

    //TODO
    /**
     * Create a new instance of this class by passing in an outer [LineString] and optionally
     * one or more inner LineStrings. Each of these LineStrings should follow the linear ring rules.
     *
     *
     * Note that if a LineString breaks one of the linear ring rules, a [RuntimeException] will
     * be thrown.
     *
     * @param outer a LineString which defines the outer perimeter of the polygon
     * @param inner one or more LineStrings representing holes inside the outer perimeter
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 3.0.0
     */
    constructor(outer: LineString, inner: List<LineString>) : this(outer, inner, null)

    //TODO
    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Polygon geometry
     * @since 1.0.0
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        //TODO
        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Polygon object from scratch it is better to use the constructor.
         * For a valid Polygon to exist, it must follow the linear ring rules and the first list of
         * coordinates are considered the outer ring by default.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Polygon
         * @return a new instance of this class defined by the values in the JSON string method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Polygon = json.decodeFromString(jsonString)
    }
}