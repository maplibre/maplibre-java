package org.maplibre.geojson.model

import kotlinx.serialization.Serializable
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

//TODO
/**
 * Generic implementation for all GeoJson objects defining common traits that each GeoJson object
 * has. This logic is carried over to [Geometry] which is an interface which all seven GeoJson
 * geometries implement.
 *
 * @since 1.0.0
 */
@Serializable
sealed interface GeoJson {

    //TODO
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
    val boundingBox: BoundingBox?

    //TODO
    /**
     * This takes the currently defined values found inside the GeoJson instance and converts it to a
     * GeoJson string.
     *
     * @return a JSON string which represents this Feature
     * @since 1.0.0
     */
    fun toJson(): String

    companion object {

        //TODO
        /**
         * Create a new GeoJSON instance that is a sub class of this GeoJSON interface.
         *
         * @param jsonString a formatted valid JSON string defining a Geometry
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): GeoJson = json.decodeFromString(jsonString)
    }
}
