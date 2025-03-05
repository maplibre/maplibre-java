package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON feature.
 *
 * See [Feature specification](https://tools.ietf.org/html/rfc7946#section-3.2) for more details.
 *
 * @param geometry the containing geometry of this feature.
 * @param properties additional custom properties for this feature.
 * @param id a unique identifier of this feature.
 * @param boundingBox the bounding box of this feature.
 */
@Serializable
@SerialName("Feature")
data class Feature(
    /**
     * The containing geometry of this feature.
     */
    val geometry: Geometry? = null,

    //TODO(fabi755): can we use here something generic (not from kotlinx) instead?
    //  any idea?
    /**
     * Additional custom properties for this feature.
     */
    val properties: JsonObject? = null,

    /**
     * A unique identifier of this feature.
     */
    val id: String? = null,

    /**
     * The [BoundingBox] of this feature.
     */
    @SerialName("bbox")
    override val boundingBox: BoundingBox? = null,
) : GeoJson {

    /**
     * Constructor to create an empty feature
     */
    constructor(): this(null, null, null, null)

    /**
     * Constructor to create a feature with [Geometry].
     *
     * @param geometry The geometry of this feature.
     */
    constructor(geometry: Geometry?): this(geometry, null, null, null)

    /**
     * Constructor to create a feature with [Geometry] and properties.
     *
     * @param geometry The geometry of this feature.
     * @param properties The properties of this feature.
     */
    constructor(geometry: Geometry?, properties: JsonObject?): this(geometry, properties, null, null)

    /**
     * Constructor to create a feature with [Geometry], properties and id.
     *
     * @param geometry The geometry of this feature.
     * @param properties The properties of this feature.
     * @param id The id of this feature.
     */
    constructor(geometry: Geometry?, properties: JsonObject?, id: String?): this(geometry, properties, id, null)

    /**
     * Converts this [Feature] to its GeoJSON representation, as [String].
     *
     * @return a [String] that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [Feature] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON [String]
         */
        @JvmStatic
        fun fromJson(jsonString: String): Feature = json.decodeFromString(jsonString)
    }
}
