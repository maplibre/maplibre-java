package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PolygonSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON Polygon. Polygons are built by an outer [LineString] ring and
 * zero or more inner [LineString] rings.
 *
 * Every ring (inner, and outer) are a closed linear line rings. This means, that minimum four points are required,
 * and the first and last points must be identical.
 *
 * See [Polygon specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.6) for more details.
 *
 * @param outerLineStringRing the outer [LineString] ring of this Polygon
 * @param holeLineStringRings the inner [LineString] rings of this Polygon. Or empty list if no inner holes exists
 * @param boundingBox the BoundingBox of this Polygon
 * @throws IllegalArgumentException if one of the provided [LineString] is not valid linear rings
 */
@Serializable(with = PolygonSerializer::class)
@SerialName("Polygon")
data class Polygon(
    /**
     * The outer [LineString] ring of this Polygon.
     */
    val outerLineStringRing: LineString,

    /**
     * The inner [LineString] rings of this Polygon. Or empty list if no inner holes exists.
     */
    val holeLineStringRings: List<LineString> = emptyList(),

    /**
     * The [BoundingBox] of this Polygon.
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a Polygon with outer ring.
     *
     * @param outer the outer LineString ring
     */
    constructor(outer: LineString) : this(outer, emptyList(), null)

    /**
     * Constructor to create a Polygon with outer and inner rings.
     *
     * @param outer the outer LineString ring
     * @param inner the inner LineString rings
     */
    constructor(outer: LineString, inner: List<LineString>) : this(outer, inner, null)

    /**
     * Check on initializing, if this Polygon is valid.
     *
     * @throws IllegalArgumentException if the [Polygon] is invalid
     */
    init {
        outerLineStringRing.requireLinearRing()
        holeLineStringRings.forEach { line -> line.requireLinearRing() }
    }

    /**
     * Converts this [Polygon] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson(): String = json.encodeToString(this)

    /**
     * Check if given [LineString] is a linear ring.
     *
     * That means:
     * 1. Minimum four points are contained.
     * 2. First and last points are identical.
     *
     * @throws IllegalArgumentException if the [LineString] is not a linear ring
     */
    private fun LineString.requireLinearRing() {
        require(points.size >= 4) {
            "LinearString for Polygon rings need to be made up of 4 or more Points."
        }

        require(points.first() == points.last()) {
            "LinearString for Polygon rings require first and last Point to be identical."
        }
    }

    companion object {

        /**
         * Create a new [Polygon] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): Polygon = json.decodeFromString(jsonString)
    }
}