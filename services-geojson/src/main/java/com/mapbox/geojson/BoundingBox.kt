package com.mapbox.geojson

import androidx.annotation.FloatRange
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.mapbox.geojson.constants.GeoJsonConstants
import com.mapbox.geojson.gson.BoundingBoxTypeAdapter
import java.io.Serializable

/**
 * A GeoJson object MAY have a member named "bbox" to include information on the coordinate range
 * for its Geometries, Features, or FeatureCollections.
 *
 *
 * This class simplifies the build process for creating a bounding box and working with them when
 * deserialized. specific parameter naming helps define which coordinates belong where when a
 * bounding box instance is being created. Note that since GeoJson objects only have the option of
 * including a bounding box JSON element, the `bbox` value returned by a GeoJson object might
 * be null.
 *
 *
 * At a minimum, a bounding box will have two [Point]s or four coordinates which define the
 * box. A 3rd dimensional bounding box can be produced if elevation or altitude is defined.
 *
 * @since 3.0.0
 */
@Keep
class BoundingBox internal constructor(southwest: Point?, northeast: Point?) : Serializable {
    private val southwest: Point
    private val northeast: Point

    init {
        if (southwest == null) {
            throw NullPointerException("Null southwest")
        }
        this.southwest = southwest
        if (northeast == null) {
            throw NullPointerException("Null northeast")
        }
        this.northeast = northeast
    }

    /**
     * Provides the [Point] which represents the southwest corner of this bounding box when the
     * map is facing due north.
     *
     * @return a [Point] which defines this bounding boxes southwest corner
     * @since 3.0.0
     */
    fun southwest(): Point {
        return southwest
    }

    /**
     * Provides the [Point] which represents the northeast corner of this bounding box when the
     * map is facing due north.
     *
     * @return a [Point] which defines this bounding boxes northeast corner
     * @since 3.0.0
     */
    fun northeast(): Point {
        return northeast
    }

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun west(): Double {
        return southwest().longitude()
    }

    /**
     * Convenience method for getting the bounding box most southerly point (latitude) as a double
     * coordinate.
     *
     * @return the most southerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun south(): Double {
        return southwest().latitude()
    }

    /**
     * Convenience method for getting the bounding box most easterly point (longitude) as a double
     * coordinate.
     *
     * @return the most easterly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun east(): Double {
        return northeast().longitude()
    }

    /**
     * Convenience method for getting the bounding box most westerly point (longitude) as a double
     * coordinate.
     *
     * @return the most westerly coordinate inside this bounding box
     * @since 3.0.0
     */
    fun north(): Double {
        return northeast().latitude()
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Bounding box
     * @since 3.0.0
     */
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(BoundingBox::class.java, BoundingBoxTypeAdapter())
            .create()
        return gson.toJson(this, BoundingBox::class.java)
    }

    override fun toString(): String {
        return ("BoundingBox{"
                + "southwest=" + southwest + ", "
                + "northeast=" + northeast
                + "}")
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other is BoundingBox) {
            return (southwest == other.southwest()
                    && northeast == other.northeast())
        }
        return false
    }

    override fun hashCode(): Int {
        var hashCode = 1
        hashCode *= 1000003
        hashCode = hashCode xor southwest.hashCode()
        hashCode *= 1000003
        hashCode = hashCode xor northeast.hashCode()
        return hashCode
    }

    companion object {
        /**
         * Create a new instance of this class by passing in a formatted valid JSON String.
         *
         * @param json a formatted valid JSON string defining a Bounding Box
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        fun fromJson(json: String?): BoundingBox {
            val gson = GsonBuilder()
                .registerTypeAdapter(BoundingBox::class.java, BoundingBoxTypeAdapter())
                .create()
            return gson.fromJson(json, BoundingBox::class.java)
        }

        /**
         * Define a new instance of this class by passing in two [Point]s, representing both the
         * southwest and northwest corners of the bounding box.
         *
         * @param southwest represents the bottom left corner of the bounding box when the camera is
         * pointing due north
         * @param northeast represents the top right corner of the bounding box when the camera is
         * pointing due north
         * @return a new instance of this class defined by the provided points
         * @since 3.0.0
         */
        @JvmStatic
        fun fromPoints(southwest: Point, northeast: Point): BoundingBox {
            return BoundingBox(southwest, northeast)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west  the left side of the bounding box when the map is facing due north
         * @param south the bottom side of the bounding box when the map is facing due north
         * @param east  the right side of the bounding box when the map is facing due north
         * @param north the top side of the bounding box when the map is facing due north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.0.0
         */
        @Deprecated("As of 3.1.0, use {@link #fromLngLats} instead.", ReplaceWith("fromLngLats"))
        fun fromCoordinates(
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) west: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) south: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) east: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) north: Double
        ): BoundingBox {
            return fromLngLats(west, south, east, north)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west              the left side of the bounding box when the map is facing due north
         * @param south             the bottom side of the bounding box when the map is facing due north
         * @param southwestAltitude the southwest corner altitude or elevation when the map is facing due
         * north
         * @param east              the right side of the bounding box when the map is facing due north
         * @param north             the top side of the bounding box when the map is facing due north
         * @param northEastAltitude the northeast corner altitude or elevation when the map is facing due
         * north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.0.0
         */
        @Deprecated("As of 3.1.0, use {@link #fromLngLats} instead.", ReplaceWith("frmLngLats"))
        fun fromCoordinates(
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) west: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) south: Double,
            southwestAltitude: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) east: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) north: Double,
            northEastAltitude: Double
        ): BoundingBox {
            return fromLngLats(west, south, southwestAltitude, east, north, northEastAltitude)
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west  the left side of the bounding box when the map is facing due north
         * @param south the bottom side of the bounding box when the map is facing due north
         * @param east  the right side of the bounding box when the map is facing due north
         * @param north the top side of the bounding box when the map is facing due north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.1.0
         */
        @JvmStatic
        fun fromLngLats(
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) west: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) south: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) east: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) north: Double
        ): BoundingBox {
            return BoundingBox(
                Point.fromLngLat(west, south),
                Point.fromLngLat(east, north)
            )
        }

        /**
         * Define a new instance of this class by passing in four coordinates in the same order they would
         * appear in the serialized GeoJson form. Limits are placed on the minimum and maximum coordinate
         * values which can exist and comply with the GeoJson spec.
         *
         * @param west              the left side of the bounding box when the map is facing due north
         * @param south             the bottom side of the bounding box when the map is facing due north
         * @param southwestAltitude the southwest corner altitude or elevation when the map is facing due
         * north
         * @param east              the right side of the bounding box when the map is facing due north
         * @param north             the top side of the bounding box when the map is facing due north
         * @param northEastAltitude the northeast corner altitude or elevation when the map is facing due
         * north
         * @return a new instance of this class defined by the provided coordinates
         * @since 3.1.0
         */
        @JvmStatic
        fun fromLngLats(
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) west: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) south: Double,
            southwestAltitude: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LONGITUDE,
                to = GeoJsonConstants.MAX_LONGITUDE
            ) east: Double,
            @FloatRange(
                from = GeoJsonConstants.MIN_LATITUDE,
                to = GeoJsonConstants.MAX_LATITUDE
            ) north: Double,
            northEastAltitude: Double
        ): BoundingBox {
            return BoundingBox(
                Point.fromLngLat(west, south, southwestAltitude),
                Point.fromLngLat(east, north, northEastAltitude)
            )
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @JvmStatic
        fun typeAdapter(@Suppress("UNUSED_PARAMETER") gson: Gson?): TypeAdapter<BoundingBox> {
            return BoundingBoxTypeAdapter()
        }
    }
}