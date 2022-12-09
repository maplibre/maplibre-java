package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import com.mapbox.geojson.shifter.CoordinateShifterManager
import java.io.IOException

/**
 * A point represents a single geographic position and is one of the seven Geometries found in the
 * GeoJson spec.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * Coordinates are in x, y order (easting, northing for projected coordinates), longitude, and
 * latitude for geographic coordinates), precisely in that order and using double values. Altitude
 * or elevation MAY be included as an optional third parameter while creating this object.
 *
 *
 * The size of a GeoJson text in bytes is a major interoperability consideration, and precision of
 * coordinate values has a large impact on the size of texts when serialized. For geographic
 * coordinates with units of degrees, 6 decimal places (a default common in, e.g., sprintf) amounts
 * to about 10 centimeters, a precision well within that of current GPS systems. Implementations
 * should consider the cost of using a greater precision than necessary.
 *
 *
 * Furthermore, pertaining to altitude, the WGS 84 datum is a relatively coarse approximation of the
 * geoid, with the height varying by up to 5 m (but generally between 2 and 3 meters) higher or
 * lower relative to a surface parallel to Earth's mean sea level.
 *
 *
 * A sample GeoJson Point's provided below (in its serialized state).
 * <pre>
 * {
 * "type": "Point",
 * "coordinates": [100.0, 0.0]
 * }
</pre> *
 *
 * @since 1.0.0
 */
@Keep
class Point internal constructor(type: String, bbox: BoundingBox?, coordinates: List<Double>?) :
    CoordinateContainer<List<Double>?> {
    private val type: String
    private val bbox: BoundingBox?
    private val coordinates: List<Double>

    init {
        this.type = type
        this.bbox = bbox
        if (coordinates.isNullOrEmpty()) {
            throw NullPointerException("Null coordinates")
        }
        this.coordinates = coordinates
    }

    /**
     * This returns a double value representing the x or easting position of
     * this point. ideally, this value would be restricted to 6 decimal places to correctly follow the
     * GeoJson spec.
     *
     * @return a double value representing the x or easting position of this
     * point
     * @since 3.0.0
     */
    fun longitude(): Double {
        return coordinates()[0]
    }

    /**
     * This returns a double value representing the y or northing position of
     * this point. ideally, this value would be restricted to 6 decimal places to correctly follow the
     * GeoJson spec.
     *
     * @return a double value representing the y or northing position of this
     * point
     * @since 3.0.0
     */
    fun latitude(): Double {
        return coordinates()[1]
    }

    /**
     * Optionally, the coordinate spec in GeoJson allows for altitude values to be placed inside the
     * coordinate array. [.hasAltitude] can be used to determine if this value was set during
     * initialization of this Point instance. This double value should only be used to represent
     * either the elevation or altitude value at this particular point.
     *
     * @return a double value ranging from negative to positive infinity
     * @since 3.0.0
     */
    fun altitude(): Double {
        return if (coordinates().size < 3) {
            Double.NaN
        } else coordinates()[2]
    }

    /**
     * Optionally, the coordinate spec in GeoJson allows for altitude values to be placed inside the
     * coordinate array. If an altitude value was provided while initializing this instance, this will
     * return true.
     *
     * @return true if this instance of point contains an altitude value
     * @since 3.0.0
     */
    fun hasAltitude(): Boolean {
        return !java.lang.Double.isNaN(altitude())
    }

    /**
     * This describes the TYPE of GeoJson geometry this object is, thus this will always return
     * [Point].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `Point`
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
     * Provide a single double array containing the longitude, latitude, and optionally an
     * altitude/elevation. [.longitude], [.latitude], and [.altitude] are all
     * avaliable which make getting specific coordinates more direct.
     *
     * @return a double array which holds this points coordinates
     * @since 3.0.0
     */
    override fun coordinates(): List<Double> {
        return coordinates
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Point geometry
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        return gson.create().toJson(this)
    }

    override fun toString(): String {
        return ("Point{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "coordinates=" + coordinates
                + "}")
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other is Point) {
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
     * TypeAdapter for Point geometry.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(gson: Gson) :
        BaseGeometryTypeAdapter<Point, List<Double>?>(gson, ListOfDoublesCoordinatesTypeAdapter()) {
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, obj: Point) {
            writeCoordinateContainer(jsonWriter, obj)
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): Point {
            return readCoordinateContainer(jsonReader) as Point
        }

        override fun createCoordinateContainer(
            type: String?,
            bbox: BoundingBox?,
            coordinates: List<Double>?
        ): CoordinateContainer<List<Double>?> {
            return Point(type ?: "Point", bbox, coordinates)
        }

    }

    companion object {
        private const val TYPE = "Point"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a Point object from scratch it is better to use one of the other provided static
         * factory methods such as [.fromLngLat]. While no limit is placed
         * on decimal precision, for performance reasons when serializing and deserializing it is
         * suggested to limit decimal precision to within 6 decimal places.
         *
         * @param json a formatted valid JSON string defining a GeoJson Point
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): Point {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            return gson.create().fromJson(json, Point::class.java)
        }

        /**
         * Create a new instance of this class defining a longitude and latitude value in that respective
         * order. While no limit is placed on decimal precision, for performance reasons
         * when serializing and deserializing it is suggested to limit decimal precision to within 6
         * decimal places.
         *
         * @param longitude a double value representing the x position of this point
         * @param latitude  a double value representing the y position of this point
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLat(longitude: Double, latitude: Double): Point {
            val coordinates =
                CoordinateShifterManager.getCoordinateShifter().shiftLonLat(longitude, latitude)
            return Point(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class defining a longitude and latitude value in that respective
         * order. While no limit is placed on decimal precision, for performance reasons
         * when serializing and deserializing it is suggested to limit decimal precision to within 6
         * decimal places. An optional altitude value can be passed in and can vary between negative
         * infinity and positive infinity.
         *
         * @param longitude a double value representing the x position of this point
         * @param latitude  a double value representing the y position of this point
         * @param bbox      optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLat(
            longitude: Double, latitude: Double,
            bbox: BoundingBox?
        ): Point {
            val coordinates =
                CoordinateShifterManager.getCoordinateShifter().shiftLonLat(longitude, latitude)
            return Point(TYPE, bbox, coordinates)
        }

        /**
         * Create a new instance of this class defining a longitude and latitude value in that respective
         * order. While no limit is placed on decimal precision, for performance reasons
         * when serializing and deserializing it is suggested to limit decimal precision to within 6
         * decimal places. An optional altitude value can be passed in and can vary between negative
         * infinity and positive infinity.
         *
         * @param longitude a double value representing the x position of this point
         * @param latitude  a double value representing the y position of this point
         * @param altitude  a double value which can be negative or positive infinity representing either
         * elevation or altitude
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLat(longitude: Double, latitude: Double, altitude: Double): Point {
            val coordinates = CoordinateShifterManager.getCoordinateShifter()
                .shiftLonLatAlt(longitude, latitude, altitude)
            return Point(TYPE, null, coordinates)
        }

        /**
         * Create a new instance of this class defining a longitude and latitude value in that respective
         * order. While no limit is placed on decimal precision, for performance reasons
         * when serializing and deserializing it is suggested to limit decimal precision to within 6
         * decimal places. An optional altitude value can be passed in and can vary between negative
         * infinity and positive infinity.
         *
         * @param longitude a double value representing the x position of this point
         * @param latitude  a double value representing the y position of this point
         * @param altitude  a double value which can be negative or positive infinity representing either
         * elevation or altitude
         * @param bbox      optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromLngLat(
            longitude: Double, latitude: Double,
            altitude: Double, bbox: BoundingBox?
        ): Point {
            val coordinates = CoordinateShifterManager.getCoordinateShifter()
                .shiftLonLatAlt(longitude, latitude, altitude)
            return Point(TYPE, bbox, coordinates)
        }

        @JvmStatic
        fun fromLngLat(coords: DoubleArray): Point? {
            if (coords.size == 2) {
                return fromLngLat(coords[0], coords[1])
            } else if (coords.size > 2) {
                return fromLngLat(coords[0], coords[1], coords[2])
            }
            return null
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<Point> {
            return GsonTypeAdapter(gson)
        }
    }
}