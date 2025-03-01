package org.maplibre.geojson.model

import kotlinx.serialization.Serializable
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Geometry interface that is implemented by all of the six GeoJSON geometries and also [GeometryCollection].
 * Possible geometries are [Point], [MultiPoint], [LineString], [MultiLineString], [Polygon],
 * [MultiPolygon].
 *
 * See [Geometry specification](https://tools.ietf.org/html/rfc7946#section-3.1) for more details.
 *
 * @see Point
 * @see MultiPoint
 * @see LineString
 * @see MultiLineString
 * @see Polygon
 * @see MultiPolygon
 * @see GeometryCollection
 */
@Serializable
sealed interface Geometry : GeoJson {

    companion object {

        /**
         * Create a new [Geometry] from a GeoJSON representation. This will return one of [Point],
         * [MultiPoint], [LineString], [MultiLineString], [Polygon], [MultiPolygon]
         * or [GeometryCollection] instances.
         *
         * @param jsonString the GeoJSON [String]
         */
        @JvmStatic
        fun fromJson(jsonString: String): Geometry = json.decodeFromString(jsonString)
    }
}
