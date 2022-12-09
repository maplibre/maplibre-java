package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import java.io.IOException
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonReader
import java.lang.NullPointerException
import com.google.gson.GsonBuilder
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import com.mapbox.geojson.utils.PolylineUtils
import java.util.ArrayList

/**
 * A linestring represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * The list of points must be equal to or greater than 2. A LineString has non-zero length and
 * zero area. It may approximate a curve and need not be straight. Unlike a LinearRing, a LineString
 * is not closed.
 *
 *
 * When representing a LineString that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson LineString's provided below (in it's serialized state).
 * <pre>
 * {
 * "TYPE": "LineString",
 * "coordinates": [
 * [100.0, 0.0],
 * [101.0, 1.0]
 * ]
 * }
</pre> *
 * Look over the [Point] documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @since 1.0.0
 */
@Keep
class LineString internal constructor(
    type: String?,
    bbox: BoundingBox?,
    coordinates: List<Point>?
) : CoordinateContainer<List<Point>?> {
    private val type: String
    private val bbox: BoundingBox?
    private val coordinates: List<Point>

    init {
        if (type == null) {
            throw NullPointerException("Null type")
        }
        this.type = type
        this.bbox = bbox
        if (coordinates == null) {
            throw NullPointerException("Null coordinates")
        }
        this.coordinates = coordinates
    }

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [LineString].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `LineString`
     * @since 1.0.0
     */
    override fun type(): String {
        return type
    }

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
     * point. The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? {
        return bbox
    }

    /**
     * Provides the list of [Point]s that make up the LineString geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<Point> {
        return coordinates
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this LineString geometry
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        return gson.create().toJson(this)
    }

    /**
     * Encode this LineString into a Polyline string for easier serializing. When passing geometry
     * information over a mobile network connection, encoding the geometry first will generally result
     * in less bandwidth usage.
     *
     * @param precision the encoded precision which fits your best use-case
     * @return a string describing the geometry of this LineString
     * @since 1.0.0
     */
    fun toPolyline(precision: Int): String {
        return PolylineUtils.encode(coordinates(), precision)
    }

    override fun toString(): String {
        return ("LineString{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "coordinates=" + coordinates
                + "}")
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other is LineString) {
            return (type == other.type()
                    && (if (bbox == null) other.bbox() == null else bbox == other.bbox())
                    && coordinates == other.coordinates())
        }
        return false
    }

    override fun hashCode(): Int {
        var hashCode = 1
        hashCode *= 1000003
        hashCode = hashCode xor type.hashCode()
        hashCode *= 1000003
        hashCode = hashCode xor (bbox?.hashCode() ?: 0)
        hashCode *= 1000003
        hashCode = hashCode xor coordinates.hashCode()
        return hashCode
    }

    /**
     * TypeAdapter for LineString geometry.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(gson: Gson) :
        BaseGeometryTypeAdapter<LineString, List<Point>?>(
            gson,
            ListOfPointCoordinatesTypeAdapter()
        ) {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, obj: LineString) {
            writeCoordinateContainer(jsonWriter, obj)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): LineString {
            return readCoordinateContainer(jsonReader) as LineString
        }

        override fun createCoordinateContainer(
            type: String?,
            bbox: BoundingBox?,
            coordinates: List<Point>?
        ): CoordinateContainer<List<Point>?> {
            return LineString(type ?: "LineString", bbox, coordinates)
        }
    }

    companion object {
        private const val TYPE = "LineString"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a LineString object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromLngLats]. For a valid lineString to exist, it must
         * have at least 2 coordinate entries. The LineString should also have non-zero distance and zero
         * area.
         *
         * @param json a formatted valid JSON string defining a GeoJson LineString
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String?): LineString {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            return gson.create().fromJson(json, LineString::class.java)
        }

        /**
         * Create a new instance of this class by defining a [MultiPoint] object and passing. The
         * multipoint object should comply with the GeoJson specifications described in the documentation.
         *
         * @param multiPoint which will make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(multiPoint: MultiPoint): LineString {
            return LineString(TYPE, null, multiPoint.coordinates())
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         *
         * Note that if less than 2 points are passed in, a runtime exception will occur.
         *
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>): LineString {
            return LineString(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list and the points combined should create a LineString with a
         * distance greater than 0.
         *
         *
         * Note that if less than 2 points are passed in, a runtime exception will occur.
         *
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @param bbox   optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(points: List<Point>, bbox: BoundingBox?): LineString {
            return LineString(TYPE, bbox, points)
        }

        /**
         * Create a new instance of this class by defining a [MultiPoint] object and passing. The
         * multipoint object should comply with the GeoJson specifications described in the documentation.
         *
         * @param multiPoint which will make up the LineString geometry
         * @param bbox       optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLats(multiPoint: MultiPoint, bbox: BoundingBox?): LineString {
            return LineString(TYPE, bbox, multiPoint.coordinates())
        }

        @JvmStatic
        fun fromLngLats(coordinates: Array<DoubleArray>): LineString {
            val converted = ArrayList<Point>(coordinates.size)
            for (coordinate in coordinates) {
                converted.add(Point.fromLngLat(coordinate)!!)
            }
            return fromLngLats(converted)
        }

        /**
         * Create a new instance of this class by convert a polyline string into a lineString. This is
         * handy when an API provides you with an encoded string representing the line geometry and you'd
         * like to convert it to a useful LineString object. Note that the precision that the string
         * geometry was encoded with needs to be known and passed into this method using the precision
         * parameter.
         *
         * @param polyline  encoded string geometry to decode into a new LineString instance
         * @param precision The encoded precision which must match the same precision used when the string
         * was first encoded
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @Suppress("unused")
        @JvmStatic
        fun fromPolyline(polyline: String, precision: Int): LineString {
            return fromLngLats(PolylineUtils.decode(polyline, precision), null)
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<LineString> {
            return GsonTypeAdapter(gson)
        }
    }
}