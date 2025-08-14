package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiPointSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON MultiPoint. MultiPoints are built by an array of [Point]s.
 * Minimum one Point item is required.
 *
 * See [MultiPoint specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.3) for more details.
 *
 * @param points [Point] list held by this instance
 * @param boundingBox [BoundingBox] of this multi point
 * @throws IllegalArgumentException if the given list of [Point]s is empty
 */
@Serializable(with = MultiPointSerializer::class)
@SerialName("MultiPoint")
data class MultiPoint(
    /**
     * [Point] list that held by this instance.
     */
    val points: List<Point>,

    /**
     * Bounding box of this MultiPoint.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a MultiPoint with [Point]s.
     *
     * @param points [Point] list, that represents this multi point
     * @throws IllegalArgumentException if the given list of [Point]s is empty
     */
    constructor(points: List<Point>) : this(points, null)

    /**
     * Check on initializing, if this MultiPoint is valid.
     * @throws IllegalArgumentException if the given list of [Point]s is empty
     */
    init {
        require(points.isNotEmpty()) { "MultiPoint must have at least one Point" }
    }

    /**
     * Converts this [MultiPoint] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [MultiPoint] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPoint = json.decodeFromString(jsonString)
    }
}
