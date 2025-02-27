package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic


//TODO
/**
 * This represents a GeoJson Feature Collection which holds a list of [Feature] objects (when
 * serialized the feature list becomes a JSON array).
 *
 *
 * Note that the feature list could potentially be empty. Features within the list must follow the
 * specifications defined inside the [Feature] class.
 *
 *
 * An example of a Feature Collections given below:
 * ```json
 * {
 *   "TYPE": "FeatureCollection",
 *   "bbox": [100.0, 0.0, -100.0, 105.0, 1.0, 0.0],
 *   "features": [
 *     //...
 *   ]
 * }
 * ```
 *
 * //TODO
 * @param features a list of features
 * @param bbox     optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("FeatureCollection")
data class FeatureCollection(
    //TODO
    val features: List<Feature>,
    //TODO
    override val bbox: BoundingBox?,
) : GeoJson {

    //TODO
    /**
     * Create a new instance by giving the feature collection a list of [Feature] objects.
     *
     * @param features a list of features
     */
    constructor(features: List<Feature>) : this(features, null)

    //TODO
    /**
     * Create a new instance by giving the feature collection a single [Feature].
     *
     * @param feature a single feature
     */
    constructor(feature: Feature) : this(listOf(feature), null)

    //TODO
    /**
     * Create a new instance by giving the feature collection a single [Feature].
     *
     * @param feature a single feature
     * @param bbox    optionally include a bbox definition as a double array
     */
    constructor(feature: Feature, bbox: BoundingBox) : this(listOf(feature), bbox)

    //TODO
    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Feature Collection
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        //TODO
        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a FeatureCollection object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Feature Collection
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): FeatureCollection = json.decodeFromString(jsonString)
    }
}
