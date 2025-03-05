package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON feature collection.
 *
 * See [FeatureCollection specification](https://tools.ietf.org/html/rfc7946#section-3.3) for more details.
 *
 * @param features [Feature]s that holds by this collection.
 * @param boundingBox [BoundingBox] for the feature collection.
 */
@Serializable
@SerialName("FeatureCollection")
data class FeatureCollection(
    /**
     * [Feature]s that holds by this collection.
     */
    val features: List<Feature>,

    /**
     * The [BoundingBox] of this collection.
     */
    @SerialName("bbox")
    override val boundingBox: BoundingBox? = null,
) : GeoJson {

    /**
     * Constructor to create a [FeatureCollection] with [Feature]s.
     */
    constructor(features: List<Feature>) : this(features, null)

    /**
     * Converts this [FeatureCollection] to its GeoJSON representation, as [String].
     *
     * @return a [String] that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [FeatureCollection] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON [String]
         */
        @JvmStatic
        fun fromJson(jsonString: String): FeatureCollection = json.decodeFromString(jsonString)
    }
}
