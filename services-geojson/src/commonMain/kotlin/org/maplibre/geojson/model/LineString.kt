package org.maplibre.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.maplibre.geojson.serializer.LineStringSerializer
import org.maplibre.geojson.utils.PolylineUtils
import org.maplibre.geojson.utils.json
import kotlin.jvm.JvmStatic

/**
 *
 */
@Serializable(with = LineStringSerializer::class)
@SerialName("LineString")
data class LineString(
    /**
     *
     */
    val points: List<Point>,

    /**
     *
     */
    override val boundingBox: BoundingBox?
) : Geometry {

    /**
     *
     */
    constructor(coordinates: List<Point>) : this(coordinates, null)

    /**
     *
     */
    init {
        require(points.size >= 2) { "LineString must have at least two Points" }
    }

    /**
     *
     */
    fun toPolyline(precision: Int): String {
        return PolylineUtils.encode(points, precision)
    }

    /**
     *
     */
    override fun toJson(): String = json.encodeToString(this)

    companion object {

        /**
         *
         */
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int) = fromPolyline(polyline, precision, null)

        /**
         *
         */
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int, bbox: BoundingBox?): LineString {
            val points = PolylineUtils.decode(polyline, precision)
            return LineString(points, bbox)
        }

        /**
         *
         */
        @JvmStatic
        fun fromJson(jsonString: String): LineString = json.decodeFromString(jsonString)
    }
}

