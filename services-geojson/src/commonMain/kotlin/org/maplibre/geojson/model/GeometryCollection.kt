package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 * Model representing a GeoJSON geometry collection. as the name already suggests,
 * [GeometryCollection] holds a list of [Geometry]s.
 *
 * See [GeometryCollection specification](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.8) for more details.
 *
 * @param geometries [Geometry]s that holds by this collection.
 * @param boundingBox [BoundingBox] for the geometry collection.
 */
@Serializable
@SerialName("GeometryCollection")
data class GeometryCollection(
    /**
     * [Geometry]s hold by this collection.
     */
    val geometries: List<Geometry>,

    /**
     * The [BoundingBox] of this collection.
     */
    @SerialName("bbox")
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     * Constructor to create a collection with [Geometry]s.
     *
     * @param geometries geometries of this collection.
     */
    constructor(geometries: List<Geometry>) : this(geometries, null)

    /**
     * Converts this [GeometryCollection] to its GeoJSON representation, as String.
     *
     * @return a String that contains JSON
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         * Create a new [GeometryCollection] from a GeoJSON representation.
         *
         * @param jsonString the GeoJSON String
         */
        @JvmStatic
        fun fromJson(jsonString: String): GeometryCollection = json.decodeFromString(jsonString)
    }
}
