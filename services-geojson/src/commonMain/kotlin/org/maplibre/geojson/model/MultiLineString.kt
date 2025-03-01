package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.MultiLineStringSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = MultiLineStringSerializer::class)
@SerialName("MultiLineString")
data class MultiLineString(
    /**
     *
     */
    val lineStrings: List<LineString>,

    /**
     *
     */
    override val boundingBox: BoundingBox?,
) : Geometry {

    /**
     *
     */
    constructor(lineStrings: List<LineString>) : this(lineStrings, null)

    /**
     *
     */
    init {
        require(lineStrings.isNotEmpty()) { "MultiLineString must have at least one LineString" }
    }

    /**
     *
     */
    override fun toJson() = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiLineString = json.decodeFromString(jsonString)
    }
}
