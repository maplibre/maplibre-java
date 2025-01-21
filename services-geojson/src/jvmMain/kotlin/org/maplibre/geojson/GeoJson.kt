package org.maplibre.geojson

import org.maplibre.geojson.common.toJvm
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.GeometryCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import org.maplibre.geojson.model.GeoJson as CommonGeoJson
import java.io.Serializable

/**
 * Generic implementation for all GeoJson objects defining common traits that each GeoJson object
 * has. This logic is carried over to [Geometry] which is an interface which all seven GeoJson
 * geometries implement.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("GeoJson", "org.maplibre.geojson.model.GeoJson"),
)
interface GeoJson : Serializable {
    /**
     * This describes the type of GeoJson geometry, Feature, or FeatureCollection this object is.
     * Every GeoJson Object will have this defined once an instance is created and will never return
     * null.
     *
     * @return a String which describes the type of geometry, for this object it will always return
     * `Feature`
     * @since 1.0.0
     */
    fun type(): String

    /**
     * This takes the currently defined values found inside the GeoJson instance and converts it to a
     * GeoJson string.
     *
     * @return a JSON string which represents this Feature
     * @since 1.0.0
     */
    fun toJson(): String

    /**
     * A GeoJson object MAY have a member named "bbox" to include information on the coordinate range
     * for its Geometries, Features, or FeatureCollections.  The value of the bbox member MUST be an
     * array of length 2*n where n is the number of dimensions represented in the contained
     * geometries, with all axes of the most southwesterly point followed by all axes of the more
     * northeasterly point.  The axes order of a bbox follows the axes order of geometries.
     *
     * @return a double array with the length 2*n where n is the number of dimensions represented in
     * the contained geometries
     * @since 3.0.0
     */
    fun bbox(): BoundingBox?

    companion object {

        /**
         * Create a new instance of GeoJSIN class by passing in a formatted valid JSON String.
         *
         * @param json a formatted valid JSON string defining a GeoJson Geometry
         * @return a new instance of Geometry class defined by the values passed inside
         * this static factory method
         * @since 4.0.0
         */
        @JvmStatic
        fun fromJson(json: String): GeoJson {
            return when (val commonGeoJson = CommonGeoJson.fromJson(json)) {
                is Feature -> commonGeoJson.toJvm()
                is FeatureCollection -> commonGeoJson.toJvm()
                is Point -> commonGeoJson.toJvm()
                is LineString -> commonGeoJson.toJvm()
                is MultiLineString -> commonGeoJson.toJvm()
                is MultiPoint -> commonGeoJson.toJvm()
                is MultiPolygon -> commonGeoJson.toJvm()
                is Polygon -> commonGeoJson.toJvm()
                is GeometryCollection -> commonGeoJson.toJvm()
            }
        }
    }
}