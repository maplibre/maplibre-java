package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiLineStringSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON MultiLineString. MultiLineStrings are built by an array of [LineString]s.
 * Minimum one LineString item is required.
 *
 * See [MultiLineString specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.5) for more details.
 *
 * @param lineStrings [LineString] list held by this instance
 * @param boundingBox [BoundingBox] of this multi line string
 * @throws IllegalArgumentException if the given list of [LineString]s is empty
 */
@Serializable(with = MultiLineStringSerializer::class)
@SerialName("MultiLineString")
data class MultiLineString(
    /**
     * List of [LineString] holds by this instance.
     */
    val lineStrings: List<LineString>,

    /**
     * Bounding box of this multi line.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a MultiLineString with [LineString]s.
     *
     * @param lineStrings [LineString] list, that represents this multi line
     * @throws IllegalArgumentException if the given list of [LineString]s is empty
     */
    constructor(lineStrings: List<LineString>) : this(lineStrings, null)

    /**
     * Check on initializing, if this MultiLineString is valid.
     * @throws IllegalArgumentException if the given list of [LineString]s is empty
     */
    init {
        require(lineStrings.isNotEmpty()) { "MultiLineString must have at least one LineString" }
    }

    /**
     * Converts this [MultiLineString] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [MultiLineString] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiLineString = json.decodeFromString(jsonString)
    }
}
