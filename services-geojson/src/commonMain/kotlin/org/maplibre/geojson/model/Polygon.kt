package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.PolygonSerializer
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = PolygonSerializer::class)
@SerialName("Polygon")
data class Polygon(
    /**
     *
     */
    val outerLineStringRing: LineString,

    /**
     *
     */
    val holeLineStringRings: List<LineString> = emptyList(),

    /**
     *
     */
    override val boundingBox: BoundingBox? = null,
) : Geometry {

    /**
     *
     */
    constructor(outer: LineString) : this(outer, emptyList(), null)

    /**
     *
     */
    constructor(outer: LineString, inner: List<LineString>) : this(outer, inner, null)

    /**
     *
     */
    init {
        outerLineStringRing.ensureIsLinearRing()
        holeLineStringRings.forEach { line -> line.ensureIsLinearRing() }
    }

    /**
     *
     */
    override fun toJson(): String = json.encodeToString(this)

    /**
     *
     */
    private fun LineString.ensureIsLinearRing() {
        require(points.size >= 4) {
            "LinearString for Polygon rings need to be made up of 4 or more Points."
        }

        require(points.first() == points.last()) {
            "LinearString for Polygon rings require first and last Point to be identical."
        }
    }

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): Polygon = json.decodeFromString(jsonString)
    }
}