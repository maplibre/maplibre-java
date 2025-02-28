package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * This defines a GeoJson Feature object which represents a spatially bound thing. Every Feature
 * object is a GeoJson object no matter where it occurs in a GeoJson text. A Feature object will
 * always have a "TYPE" member with the value "Feature".
 *
 *
 * A Feature object has a member with the name "geometry". The value of the geometry member SHALL be
 * either a Geometry object or, in the case that the Feature is unlocated, a JSON null value.
 *
 *
 * A Feature object has a member with the name "properties". The value of the properties member is
 * an object (any JSON object or a JSON null value).
 *
 *
 * If a Feature has a commonly used identifier, that identifier SHOULD be included as a member of
 * the Feature object through the [.id] method, and the value of this member is either a
 * JSON string or number.
 *
 *
 * An example of a serialized feature is given below:
 * ```json
 * {
 *   "TYPE": "Feature",
 *   "geometry": {
 *     "TYPE": "Point",
 *     "coordinates": [102.0, 0.5]
 *   },
 *   "properties": {
 *   "prop0": "value0"
 * }
 * ```
 *
 * //TODO
 * @param geometry   a single geometry which makes up this feature object
 * @param properties a map with [JsonElement]s containing the feature properties
 * @param bbox       optionally include a bbox definition as a double array
 * @param id         common identifier of this feature
 * @since 1.0.0
 */
@Serializable
@SerialName("Feature")
data class Feature(
    //TODO
    val geometry: Geometry? = null,
    //TODO
    //TODO: can we use here something generic (not from kotlinx) instead?
    //  any idea?
    var properties: JsonElement? = null,
    //TODO
    val id: String? = null,
    //TODO
    @SerialName("bbox")
    override val boundingBox: BoundingBox? = null,
) : GeoJson {

    //TODO
    /**
     * Create a new empty Feature instance.
     */
    constructor(): this(null, null, null, null)

    //TODO
    /**
     * Create a new Feature instance with a Geometry
     *
     * @param geometry   a single geometry which makes up this feature object
     */
    constructor(geometry: Geometry?): this(geometry, null, null, null)

    //TODO
    /**
     * Create a new Feature instance with given parameters.
     *
     * @param geometry   a single geometry which makes up this feature object
     * @param properties a map with [JsonElement]s containing the feature properties
     */
    constructor(geometry: Geometry?, properties: JsonElement?): this(geometry, properties, null, null)

    //TODO
    /**
     * Create a new Feature instance with given parameters.
     *
     * @param geometry   a single geometry which makes up this feature object
     * @param properties a map with [JsonElement]s containing the feature properties
     * @param id         common identifier of this feature
     */
    constructor(geometry: Geometry?, properties: JsonElement?, id: String?): this(geometry, properties, id, null)

    //TODO
    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Feature
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        //TODO
        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Feature object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Feature
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): Feature = json.decodeFromString(jsonString)
    }
}
