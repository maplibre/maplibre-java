package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable
@SerialName("GeometryCollection")
data class GeometryCollection(
    /**
     *
     */
    val geometries: List<Geometry>,

    /**
     *
     */
    @SerialName("bbox")
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     *
     */
    constructor(geometries: List<Geometry>) : this(geometries, null)

    /**
     *
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): GeometryCollection = json.decodeFromString(jsonString)
    }
}
