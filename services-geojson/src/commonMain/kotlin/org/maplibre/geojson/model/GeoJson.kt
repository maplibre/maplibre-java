package org.maplibre.geojson.model

import kotlinx.serialization.Serializable
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * The core model interface that is implemented by all available models in this package.
 *
 * See [GeoJSON specification](https://tools.ietf.org/html/rfc7946) for more details.
 *
 * @see Point
 * @see MultiPoint
 * @see LineString
 * @see MultiLineString
 * @see Polygon
 * @see MultiPolygon
 * @see GeometryCollection
 * @see Feature
 * @see FeatureCollection
 * @see BoundingBox
 * @see Geometry
 */
@Serializable
sealed interface GeoJson {

    /**
     * The [BoundingBox] of this GeoJson.
     */
    val boundingBox: BoundingBox?

    /**
     * Converts this [GeoJson] to its GeoJSON representation, as [String].
     *
     * @return a [String] that contains JSON
     */
    fun toJson(): String

    companion object {

        /**
         * Create a new [GeoJson] instance from a GeoJSON representation. This will return one of
         * all available models in this package.
         *
         * @param jsonString the GeoJSON [String]
         */
        @JvmStatic
        fun fromJson(jsonString: String): GeoJson = json.decodeFromString(jsonString)
    }
}
