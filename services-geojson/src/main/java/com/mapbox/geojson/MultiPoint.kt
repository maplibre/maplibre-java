package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import java.io.IOException

/**
 * A MultiPoint represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 *
 * This adheres to the RFC 7946 internet standard
 * when serialized into JSON. When deserialized, this class becomes an immutable object which should
 * be initiated using its static factory methods. The list of points must be equal to or greater
 * than 2.
 *
 * A sample GeoJson MultiPoint's provided below (in it's serialized state).
 * <pre>
 * {
 * "TYPE": "MultiPoint",
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
class MultiPoint internal constructor(
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
     * This describes the TYPE of GeoJson this object is, thus this will always return [ ].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `MultiPoint`
     * @since 1.0.0
     */
    override fun type(): String {
        return type
    }

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of size
     * 2*n where n is the number of dimensions represented in the contained feature geometries, with
     * all axes of the most southwesterly point followed by all axes of the more northeasterly point.
     * The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? {
        return bbox
    }

    /**
     * provides the list of [Point]s that make up the MultiPoint geometry.
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
     * @return a JSON string which represents this MultiPoint geometry
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        return gson.create().toJson(this)
    }

    override fun toString(): String {
        return ("MultiPoint{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "coordinates=" + coordinates
                + "}")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj is MultiPoint) {
            val that = obj
            return (type == that.type()
                    && (if (bbox == null) that.bbox() == null else bbox == that.bbox())
                    && coordinates == that.coordinates())
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
     * TypeAdapter for MultiPoint geometry.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(gson: Gson) :
        BaseGeometryTypeAdapter<MultiPoint, List<Point>?>(
            gson,
            ListOfPointCoordinatesTypeAdapter()
        ) {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, obj: MultiPoint?) {
            writeCoordinateContainer(jsonWriter, obj)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): MultiPoint {
            return readCoordinateContainer(jsonReader) as MultiPoint
        }

        public override fun createCoordinateContainer(
            type: String?,
            bbox: BoundingBox?,
            coordinates: List<Point>?
        ): CoordinateContainer<List<Point>?> {
            return MultiPoint(type ?: "MultiPoint", bbox, coordinates)
        }
    }

    companion object {
        private const val TYPE = "MultiPoint"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPoint object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromLngLats]. For a valid MultiPoint to exist, it must
         * have at least 2 coordinate entries.
         *
         * @param json a formatted valid JSON string defining a GeoJson MultiPoint
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromJson(json: String): MultiPoint {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            return gson.create().fromJson(json, MultiPoint::class.java)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list.
         *
         * Note that if less than 2 points are passed in, a runtime
         * exception will occur.
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromLngLats(points: List<Point>): MultiPoint {
            return MultiPoint(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of [Point]s which follow the
         * correct specifications described in the Point documentation. Note that there should not be any
         * duplicate points inside the list.
         *
         * Note that if less than 2 points are passed in, a runtime
         * exception will occur.
         *
         * @param points a list of [Point]s which make up the LineString geometry
         * @param bbox   optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromLngLats(points: List<Point>, bbox: BoundingBox?): MultiPoint {
            return MultiPoint(TYPE, bbox, points)
        }

        @kotlin.jvm.JvmStatic
        fun fromLngLats(coordinates: Array<DoubleArray>): MultiPoint {
            val converted = ArrayList<Point>(coordinates.size)
            for (i in coordinates.indices) {
                converted.add(Point.Companion.fromLngLat(coordinates[i])!!)
            }
            return MultiPoint(TYPE, null, converted)
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<MultiPoint> {
            return GsonTypeAdapter(gson)
        }
    }
}