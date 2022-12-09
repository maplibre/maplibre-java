package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryCollection
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import java.io.IOException
import java.util.*

/**
 * A GeoJson object with TYPE "GeometryCollection" is a Geometry object.
 *
 *
 * A GeometryCollection has a member with the name "geometries". The value of "geometries" is a List
 * Each element of this list is a GeoJson Geometry object. It is possible for this list to be empty.
 *
 *
 * Unlike the other geometry types, a GeometryCollection can be a heterogeneous composition of
 * smaller Geometry objects. For example, a Geometry object in the shape of a lowercase roman "i"
 * can be composed of one point and one LineString.
 *
 *
 * GeometryCollections have a different syntax from single TYPE Geometry objects (Point,
 * LineString, and Polygon) and homogeneously typed multipart Geometry objects (MultiPoint,
 * MultiLineString, and MultiPolygon) but have no different semantics.  Although a
 * GeometryCollection object has no "coordinates" member, it does have coordinates: the coordinates
 * of all its parts belong to the collection. The "geometries" member of a GeometryCollection
 * describes the parts of this composition. Implementations SHOULD NOT apply any additional
 * semantics to the "geometries" array.
 *
 *
 * To maximize interoperability, implementations SHOULD avoid nested GeometryCollections.
 * Furthermore, GeometryCollections composed of a single part or a number of parts of a single TYPE
 * SHOULD be avoided when that single part or a single object of multipart TYPE (MultiPoint,
 * MultiLineString, or MultiPolygon) could be used instead.
 *
 *
 * An example of a serialized GeometryCollections given below:
 * <pre>
 * {
 * "TYPE": "GeometryCollection",
 * "geometries": [{
 * "TYPE": "Point",
 * "coordinates": [100.0, 0.0]
 * }, {
 * "TYPE": "LineString",
 * "coordinates": [
 * [101.0, 0.0],
 * [102.0, 1.0]
 * ]
 * }]
 * }
</pre> *
 *
 * @since 1.0.0
 */
@Keep
class GeometryCollection internal constructor(
    type: String?,
    bbox: BoundingBox?,
    geometries: List<Geometry>?
) : Geometry {
    private val type: String
    private val bbox: BoundingBox?
    private val geometries: List<Geometry>

    /**
     * Create a new instance of this class by giving the collection a list of [Geometry] and
     * bounding box.
     *
     * @param geometries a non-null list of geometry which makes up this collection
     * @param bbox       optionally include a bbox definition as a double array
     * @since 4.6.0
     */
    init {
        if (type == null) {
            throw NullPointerException("Null type")
        }
        this.type = type
        this.bbox = bbox
        if (geometries == null) {
            throw NullPointerException("Null geometries")
        }
        this.geometries = geometries
    }

    /**
     * This describes the TYPE of GeoJson this object is, thus this will always return
     * [GeometryCollection].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `GeometryCollection`
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
     * This provides the list of geometry making up this Geometry Collection. Note that if the
     * Geometry Collection was created through [.fromJson] this list could be null.
     * Otherwise, the list can't be null but the size of the list can equal 0.
     *
     * @return a list of [Geometry] which make up this Geometry Collection
     * @since 1.0.0
     */
    fun geometries(): List<Geometry> {
        return geometries
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this GeometryCollection
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        gson.registerTypeAdapterFactory(GeometryAdapterFactory.create())
        return gson.create().toJson(this)
    }

    override fun toString(): String {
        return ("GeometryCollection{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "geometries=" + geometries
                + "}")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj is GeometryCollection) {
            val that = obj
            return (type == that.type()
                    && (if (bbox == null) that.bbox() == null else bbox == that.bbox())
                    && geometries == that.geometries())
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
        hashCode = hashCode xor geometries.hashCode()
        return hashCode
    }

    /**
     * TypeAdapter to serialize/deserialize GeometryCollection objects.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(private val gson: Gson) : TypeAdapter<GeometryCollection>() {
        @Volatile
        private var stringTypeAdapter: TypeAdapter<String?>? = null

        @Volatile
        private var boundingBoxTypeAdapter: TypeAdapter<BoundingBox?>? = null

        @Volatile
        private var listGeometryAdapter: TypeAdapter<List<Geometry>?>? = null
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, `object`: GeometryCollection?) {
            if (`object` == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.beginObject()
            jsonWriter.name("type")
            if (`object`.type() == null) {
                jsonWriter.nullValue()
            } else {
                var stringTypeAdapter = stringTypeAdapter
                if (stringTypeAdapter == null) {
                    stringTypeAdapter = gson.getAdapter(String::class.java)
                    this.stringTypeAdapter = stringTypeAdapter
                }
                stringTypeAdapter!!.write(jsonWriter, `object`.type())
            }
            jsonWriter.name("bbox")
            if (`object`.bbox() == null) {
                jsonWriter.nullValue()
            } else {
                var boundingBoxTypeAdapter = boundingBoxTypeAdapter
                if (boundingBoxTypeAdapter == null) {
                    boundingBoxTypeAdapter = gson.getAdapter(BoundingBox::class.java)
                    this.boundingBoxTypeAdapter = boundingBoxTypeAdapter
                }
                boundingBoxTypeAdapter!!.write(jsonWriter, `object`.bbox())
            }
            jsonWriter.name("geometries")
            if (`object`.geometries() == null) {
                jsonWriter.nullValue()
            } else {
                var listGeometryAdapter = listGeometryAdapter
                if (listGeometryAdapter == null) {
                    val typeToken = TypeToken.getParameterized(
                        MutableList::class.java, Geometry::class.java
                    )
                    listGeometryAdapter = gson.getAdapter(typeToken) as TypeAdapter<List<Geometry>?>
                    this.listGeometryAdapter = listGeometryAdapter
                }
                listGeometryAdapter!!.write(jsonWriter, `object`.geometries())
            }
            jsonWriter.endObject()
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): GeometryCollection? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            jsonReader.beginObject()
            var type: String? = null
            var bbox: BoundingBox? = null
            var geometries: List<Geometry>? = null
            while (jsonReader.hasNext()) {
                val name = jsonReader.nextName()
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull()
                    continue
                }
                when (name) {
                    "type" -> {
                        var stringTypeAdapter = stringTypeAdapter
                        if (stringTypeAdapter == null) {
                            stringTypeAdapter = gson.getAdapter(String::class.java)
                            this.stringTypeAdapter = stringTypeAdapter
                        }
                        type = stringTypeAdapter!!.read(jsonReader)
                    }
                    "bbox" -> {
                        var boundingBoxTypeAdapter = boundingBoxTypeAdapter
                        if (boundingBoxTypeAdapter == null) {
                            boundingBoxTypeAdapter = gson.getAdapter(BoundingBox::class.java)
                            this.boundingBoxTypeAdapter = boundingBoxTypeAdapter
                        }
                        bbox = boundingBoxTypeAdapter!!.read(jsonReader)
                    }
                    "geometries" -> {
                        var listGeometryAdapter = listGeometryAdapter
                        if (listGeometryAdapter == null) {
                            val typeToken = TypeToken.getParameterized(
                                MutableList::class.java, Geometry::class.java
                            )
                            listGeometryAdapter =
                                gson.getAdapter(typeToken) as TypeAdapter<List<Geometry>?>
                            this.listGeometryAdapter = listGeometryAdapter
                        }
                        geometries = listGeometryAdapter!!.read(jsonReader)
                    }
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
            return GeometryCollection(type ?: "GeometryCollection", bbox, geometries)
        }
    }

    companion object {
        private const val TYPE = "GeometryCollection"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a GeometryCollection object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromGeometries].
         *
         * @param json a formatted valid JSON string defining a GeoJson Geometry Collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromJson(json: String?): GeometryCollection {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            gson.registerTypeAdapterFactory(GeometryAdapterFactory.create())
            return gson.create().fromJson(json, GeometryCollection::class.java)
        }

        /**
         * Create a new instance of this class by giving the collection a list of [Geometry].
         *
         * @param geometries a non-null list of geometry which makes up this collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromGeometries(geometries: List<Geometry>): GeometryCollection {
            return GeometryCollection(TYPE, null, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a list of [Geometry].
         *
         * @param geometries a non-null list of geometry which makes up this collection
         * @param bbox       optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromGeometries(
            geometries: List<Geometry>,
            bbox: BoundingBox?
        ): GeometryCollection {
            return GeometryCollection(TYPE, bbox, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a single GeoJSON [Geometry].
         *
         * @param geometry a non-null object of type geometry which makes up this collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromGeometry(geometry: Geometry): GeometryCollection {
            val geometries = Arrays.asList(geometry)
            return GeometryCollection(TYPE, null, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a single GeoJSON [Geometry].
         *
         * @param geometry a non-null object of type geometry which makes up this collection
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromGeometry(
            geometry: Geometry,
            bbox: BoundingBox?
        ): GeometryCollection {
            val geometries = Arrays.asList(geometry)
            return GeometryCollection(TYPE, bbox, geometries)
        }

        /**
         * Gson TYPE adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<GeometryCollection> {
            return GsonTypeAdapter(gson)
        }
    }
}