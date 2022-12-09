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
import java.util.Arrays
import java.util.ArrayList

/**
 * A multiPolygon is an array of Polygon coordinate arrays.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * When representing a Polygon that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson MultiPolygon's provided below (in it's serialized state).
 * <pre>
 * {
 * "type": "MultiPolygon",
 * "coordinates": [
 * [
 * [
 * [102.0, 2.0],
 * [103.0, 2.0],
 * [103.0, 3.0],
 * [102.0, 3.0],
 * [102.0, 2.0]
 * ]
 * ],
 * [
 * [
 * [100.0, 0.0],
 * [101.0, 0.0],
 * [101.0, 1.0],
 * [100.0, 1.0],
 * [100.0, 0.0]
 * ],
 * [
 * [100.2, 0.2],
 * [100.2, 0.8],
 * [100.8, 0.8],
 * [100.8, 0.2],
 * [100.2, 0.2]
 * ]
 * ]
 * ]
 * }
</pre> *
 * Look over the [Polygon] documentation to get more information about
 * formatting your list of Polygon objects correctly.
 *
 * @since 1.0.0
 */
@Keep
class MultiPolygon internal constructor(
    type: String?,
    bbox: BoundingBox?,
    coordinates: List<List<List<Point>>>?
) : CoordinateContainer<List<List<List<Point>>>?> {
    private val type: String
    private val bbox: BoundingBox?
    private val coordinates: List<List<List<Point>>>?

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
     * Returns a list of polygons which make up this MultiPolygon instance.
     *
     * @return a list of [Polygon]s which make up this MultiPolygon instance
     * @since 3.0.0
     */
    fun polygons(): List<Polygon> {
        val coordinates = coordinates()
        val polygons: MutableList<Polygon> = ArrayList(coordinates!!.size)
        for (points in coordinates) {
            polygons.add(Polygon.Companion.fromLngLats(points))
        }
        return polygons
    }

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [MultiPolygon].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `MultiPolygon`
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
     * Provides the list of list of list of [Point]s that make up the MultiPolygon geometry.
     *
     * @return a list of points
     * @since 3.0.0
     */
    override fun coordinates(): List<List<List<Point>>>? {
        return coordinates
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this MultiPolygon geometry
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        return gson.create().toJson(this)
    }

    override fun toString(): String {
        return ("Polygon{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "coordinates=" + coordinates
                + "}")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj is MultiPolygon) {
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
     * TypeAdapter for MultiPolygon geometry.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(gson: Gson) :
        BaseGeometryTypeAdapter<MultiPolygon, List<List<List<Point>>>?>(
            gson,
            ListofListofListOfPointCoordinatesTypeAdapter()
        ) {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, obj: MultiPolygon?) {
            writeCoordinateContainer(jsonWriter, obj)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): MultiPolygon {
            return readCoordinateContainer(jsonReader) as MultiPolygon
        }

        public override fun createCoordinateContainer(
            type: String?,
            bbox: BoundingBox?,
            coords: List<List<List<Point>>>?
        ): CoordinateContainer<List<List<List<Point>>>?> {
            return MultiPolygon(type ?: "MultiPolygon", bbox, coords)
        }
        
    }

    companion object {
        private const val TYPE = "MultiPolygon"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiPolygon object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromPolygons].
         *
         * @param json a formatted valid JSON string defining a GeoJson MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromJson(json: String?): MultiPolygon {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            return gson.create().fromJson(json, MultiPolygon::class.java)
        }

        /**
         * Create a new instance of this class by defining a list of [Polygon] objects and passing
         * that list in as a parameter in this method. The Polygons should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygons a list of Polygons which make up this MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromPolygons(polygons: List<Polygon>): MultiPolygon {
            val coordinates: MutableList<List<List<Point>>> = ArrayList(polygons.size)
            for (polygon in polygons) {
                coordinates.add(polygon.coordinates())
            }
            return MultiPolygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of [Polygon] objects and passing
         * that list in as a parameter in this method. The Polygons should comply with the GeoJson
         * specifications described in the documentation. Optionally, pass in an instance of a
         * [BoundingBox] which better describes this MultiPolygon.
         *
         * @param polygons a list of Polygons which make up this MultiPolygon
         * @param bbox     optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromPolygons(
            polygons: List<Polygon>,
            bbox: BoundingBox?
        ): MultiPolygon {
            val coordinates: MutableList<List<List<Point>>> = ArrayList(polygons.size)
            for (polygon in polygons) {
                coordinates.add(polygon.coordinates())
            }
            return MultiPolygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by defining a single [Polygon] objects and passing
         * it in as a parameter in this method. The Polygon should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygon a single Polygon which make up this MultiPolygon
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromPolygon(polygon: Polygon): MultiPolygon {
            val coordinates = Arrays.asList(polygon.coordinates())
            return MultiPolygon(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class by defining a single [Polygon] objects and passing
         * it in as a parameter in this method. The Polygon should comply with the GeoJson
         * specifications described in the documentation.
         *
         * @param polygon a single Polygon which make up this MultiPolygon
         * @param bbox    optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromPolygon(polygon: Polygon, bbox: BoundingBox?): MultiPolygon {
            val coordinates = Arrays.asList(polygon.coordinates())
            return MultiPolygon(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class by defining a list of a list of a list of [Point]s
         * which follow the correct specifications described in the Point documentation.
         *
         * @param points a list of [Point]s which make up the MultiPolygon geometry
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromLngLats(points: List<List<List<Point>>>): MultiPolygon {
            return MultiPolygon(TYPE, null, points)
        }

        /**
         * Create a new instance of this class by defining a list of a list of a list of [Point]s
         * which follow the correct specifications described in the Point documentation.
         *
         * @param points a list of [Point]s which make up the MultiPolygon geometry
         * @param bbox   optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromLngLats(
            points: List<List<List<Point>>>,
            bbox: BoundingBox?
        ): MultiPolygon {
            return MultiPolygon(TYPE, bbox, points)
        }

        @kotlin.jvm.JvmStatic
        fun fromLngLats(coordinates: Array<Array<Array<DoubleArray>>>): MultiPolygon {
            val converted: MutableList<List<List<Point>>> = ArrayList(coordinates.size)
            for (i in coordinates.indices) {
                val innerOneList: MutableList<List<Point>> = ArrayList(
                    coordinates[i].size
                )
                for (j in coordinates[i].indices) {
                    val innerTwoList: MutableList<Point> = ArrayList(
                        coordinates[i][j].size
                    )
                    for (k in coordinates[i][j].indices) {
                        innerTwoList.add(Point.Companion.fromLngLat(coordinates[i][j][k])!!)
                    }
                    innerOneList.add(innerTwoList)
                }
                converted.add(innerOneList)
            }
            return MultiPolygon(TYPE, null, converted)
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<MultiPolygon> {
            return GsonTypeAdapter(gson)
        }
    }
}