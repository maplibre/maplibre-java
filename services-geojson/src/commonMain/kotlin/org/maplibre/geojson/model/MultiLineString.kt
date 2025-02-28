package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiLineStringSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * A multilinestring is an array of LineString coordinate arrays.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * When representing a LineString that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson MultiLineString's provided below (in it's serialized state).
 * ```json
 * {
 *   "type": "MultiLineString",
 *   "coordinates": [
 *     [
 *       [100.0, 0.0],
 *       [101.0, 1.0]
 *     ],
 *     [
 *       [102.0, 2.0],
 *       [103.0, 3.0]
 *     ]
 *   ]
 * }
 * ```
 *
 * Look over the [LineString] documentation to get more information about
 * formatting your list of linestring objects correctly.
 *
 * //TODO
 * @param coordinates a list of {@link Point}s which make up the MultiLineString geometry
 * @param bbox   optionally include a bbox definition
 * @since 1.0.0
 */
@Serializable(with = MultiLineStringSerializer::class)
@SerialName("MultiLineString")
data class MultiLineString(
    //TODO
    val lineStrings: List<LineString>,
    //TODO
    override val boundingBox: BoundingBox?,
) : Geometry {

    //TODO
    /**
     * Create a new instance by giving the MultiLineString a list of [LineString] objects.
     *
     * @param lineStrings a list of LineStrings which make up this MultiLineString
     */
    constructor(lineStrings: List<LineString>) : this(lineStrings, null)

    init {
        require(lineStrings.isNotEmpty()) { "MultiLineString must have at least one LineString" }
    }

//TODO
    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this MultiLineString geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

//TODO
        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiLineString object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson MultiLineString
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiLineString = json.decodeFromString(jsonString)
    }
}
