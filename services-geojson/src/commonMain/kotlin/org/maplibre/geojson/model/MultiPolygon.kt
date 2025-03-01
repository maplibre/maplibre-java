package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiPolygonSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = MultiPolygonSerializer::class)
@SerialName("MultiPolygon")
data class MultiPolygon(
    /**
     *
     */
    val polygons: List<Polygon>,

    /**
     *
     */
    override val boundingBox: BoundingBox?,
) : Geometry {

    /**
     *
     */
    constructor(polygons: List<Polygon>) : this(polygons, null)

    /**
     *
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiPolygon = json.decodeFromString(jsonString)
    }
}