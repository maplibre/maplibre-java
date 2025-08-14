package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiPolygonSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON MultiPolygon. MultiPolygons are built by an array of [Polygon]s.
 * Minimum one Polygon item is required.
 *
 * See [MultiPolygon specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.7) for more details.
 *
 * @param polygons [Polygon] list held by this instance
 * @param boundingBox [BoundingBox] of this multi polygon
 * @throws IllegalArgumentException if the given list of [Polygon]s is empty
 */
@Serializable(with = MultiPolygonSerializer::class)
@SerialName("MultiPolygon")
data class MultiPolygon(
    /**
     * List of [Polygon] holds by this instance.
     */
    val polygons: List<Polygon>,

    /**
     * Bounding box of this multi polygon.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a MultiPolygon with [Polygon]s.
     *
     * @param polygons [Polygon] list, that represents this multi polygon
     * @throws IllegalArgumentException if the given list of [Polygon]s is empty
     */
    constructor(polygons: List<Polygon>) : this(polygons, null)

    /**
     * Check on initializing, if this MultiPolygon is valid.
     * @throws IllegalArgumentException if the given list of [Polygon]s is empty
     */
    init {
        require(polygons.isNotEmpty()) { "MultiPolygon must have at least one Polygon" }
    }

    /**
     * Converts this [MultiPolygon] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [MultiPolygon] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPolygon = json.decodeFromString(jsonString)
    }
}