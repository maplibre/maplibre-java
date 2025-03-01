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
    constructor(multiPoint: MultiPoint) : this(multiPoint, null)

    /**
     *
     */
    constructor(multiPoint: MultiPoint, bbox: BoundingBox?) : this(
        multiPoint.points,
        bbox
    )

    /**
     *
     */
    constructor(polyline: String, precision: Int) : this(polyline, precision, null)

    /**
     *
     */
    constructor(polyline: String, precision: Int, bbox: BoundingBox?) : this(
        PolylineUtils.decode(polyline, precision),
        bbox
    )

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
        fun fromJson(jsonString: String): LineString = json.decodeFromString(jsonString)
    }
}

