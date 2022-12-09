package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.gson.BoundingBoxTypeAdapter
import com.mapbox.geojson.gson.GeoJsonAdapterFactory
import java.io.IOException
import java.util.*

/**
 * This represents a GeoJson Feature Collection which holds a list of [Feature] objects (when
 * serialized the feature list becomes a JSON array).
 *
 *
 * Note that the feature list could potentially be empty. Features within the list must follow the
 * specifications defined inside the [Feature] class.
 *
 *
 * An example of a Feature Collections given below:
 * <pre>
 * {
 * "TYPE": "FeatureCollection",
 * "bbox": [100.0, 0.0, -100.0, 105.0, 1.0, 0.0],
 * "features": [
 * //...
 * ]
 * }
</pre> *
 *
 * @since 1.0.0
 */
@Keep
class FeatureCollection internal constructor(
    type: String?,
    bbox: BoundingBox?,
    features: List<Feature>?
) : GeoJson {
    private val type: String

    @JsonAdapter(BoundingBoxTypeAdapter::class)
    private val bbox: BoundingBox?
    private val features: List<Feature>?

    init {
        if (type == null) {
            throw NullPointerException("Null type")
        }
        this.type = type
        this.bbox = bbox
        this.features = features
    }

    /**
     * This describes the type of GeoJson this object is, thus this will always return
     * [FeatureCollection].
     *
     * @return a String which describes the TYPE of GeoJson, for this object it will always return
     * `FeatureCollection`
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
     * This provides the list of feature making up this Feature Collection. Note that if the
     * FeatureCollection was created through [.fromJson] this list could be null.
     * Otherwise, the list can't be null but the size of the list can equal 0.
     *
     * @return a list of [Feature]s which make up this Feature Collection
     * @since 1.0.0
     */
    fun features(): List<Feature>? {
        return features
    }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this Feature Collection
     * @since 1.0.0
     */
    override fun toJson(): String? {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        gson.registerTypeAdapterFactory(GeometryAdapterFactory.create())
        return gson.create().toJson(this)
    }

    override fun toString(): String {
        return ("FeatureCollection{"
                + "type=" + type + ", "
                + "bbox=" + bbox + ", "
                + "features=" + features
                + "}")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj is FeatureCollection) {
            val that = obj
            return (type == that.type()
                    && (if (bbox == null) that.bbox() == null else bbox == that.bbox())
                    && if (features == null) that.features() == null else features == that.features())
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
        hashCode = hashCode xor (features?.hashCode() ?: 0)
        return hashCode
    }

    /**
     * TypeAdapter to serialize/deserialize FeatureCollection objects.
     *
     * @since 4.6.0
     */
    internal class GsonTypeAdapter(private val gson: Gson) : TypeAdapter<FeatureCollection>() {
        @Volatile
        private var stringAdapter: TypeAdapter<String?>? = null

        @Volatile
        private var boundingBoxAdapter: TypeAdapter<BoundingBox?>? = null

        @Volatile
        private var listFeatureAdapter: TypeAdapter<List<Feature>?>? = null
        @Throws(IOException::class)
        override fun write(jsonWriter: JsonWriter, obj: FeatureCollection?) {
            if (obj == null) {
                jsonWriter.nullValue()
                return
            }
            jsonWriter.beginObject()
            jsonWriter.name("type")
            if (obj.type() == null) {
                jsonWriter.nullValue()
            } else {
                var stringAdapter = stringAdapter
                if (stringAdapter == null) {
                    stringAdapter = gson.getAdapter(String::class.java)
                    this.stringAdapter = stringAdapter
                }
                stringAdapter!!.write(jsonWriter, obj.type())
            }
            jsonWriter.name("bbox")
            if (obj.bbox() == null) {
                jsonWriter.nullValue()
            } else {
                var boundingBoxTypeAdapter = boundingBoxAdapter
                if (boundingBoxTypeAdapter == null) {
                    boundingBoxTypeAdapter = gson.getAdapter(BoundingBox::class.java)
                    boundingBoxAdapter = boundingBoxTypeAdapter
                }
                boundingBoxTypeAdapter!!.write(jsonWriter, obj.bbox())
            }
            jsonWriter.name("features")
            if (obj.features() == null) {
                jsonWriter.nullValue()
            } else {
                var listFeatureAdapter = listFeatureAdapter
                if (listFeatureAdapter == null) {
                    val typeToken = TypeToken.getParameterized(
                        MutableList::class.java, Feature::class.java
                    )
                    listFeatureAdapter = gson.getAdapter(typeToken) as TypeAdapter<List<Feature>?>
                    this.listFeatureAdapter = listFeatureAdapter
                }
                listFeatureAdapter!!.write(jsonWriter, obj.features())
            }
            jsonWriter.endObject()
        }

        @Throws(IOException::class)
        override fun read(jsonReader: JsonReader): FeatureCollection? {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull()
                return null
            }
            jsonReader.beginObject()
            var type: String? = null
            var bbox: BoundingBox? = null
            var features: List<Feature>? = null
            while (jsonReader.hasNext()) {
                val name = jsonReader.nextName()
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull()
                    continue
                }
                when (name) {
                    "type" -> {
                        var stringAdapter = stringAdapter
                        if (stringAdapter == null) {
                            stringAdapter = gson.getAdapter(String::class.java)
                            this.stringAdapter = stringAdapter
                        }
                        type = stringAdapter!!.read(jsonReader)
                    }
                    "bbox" -> {
                        var boundingBoxAdapter = boundingBoxAdapter
                        if (boundingBoxAdapter == null) {
                            boundingBoxAdapter = gson.getAdapter(BoundingBox::class.java)
                            this.boundingBoxAdapter = boundingBoxAdapter
                        }
                        bbox = boundingBoxAdapter!!.read(jsonReader)
                    }
                    "features" -> {
                        var listFeatureAdapter = listFeatureAdapter
                        if (listFeatureAdapter == null) {
                            val typeToken = TypeToken.getParameterized(
                                MutableList::class.java, Feature::class.java
                            )
                            listFeatureAdapter =
                                gson.getAdapter(typeToken) as TypeAdapter<List<Feature>?>
                            this.listFeatureAdapter = listFeatureAdapter
                        }
                        features = listFeatureAdapter!!.read(jsonReader)
                    }
                    else -> jsonReader.skipValue()
                }
            }
            jsonReader.endObject()
            return FeatureCollection(type, bbox, features)
        }
    }

    companion object {
        private const val TYPE = "FeatureCollection"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a FeatureCollection object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromFeatures].
         *
         * @param json a formatted valid JSON string defining a GeoJson Feature Collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromJson(json: String): FeatureCollection {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
            gson.registerTypeAdapterFactory(GeometryAdapterFactory.create())
            return gson.create().fromJson(json, FeatureCollection::class.java)
        }

        /**
         * Create a new instance of this class by giving the feature collection an array of
         * [Feature]s. The array of features itself isn't null but it can be empty and have a length
         * of 0.
         *
         * @param features an array of features
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeatures(features: Array<Feature>): FeatureCollection {
            return FeatureCollection(TYPE, null, Arrays.asList(*features))
        }

        /**
         * Create a new instance of this class by giving the feature collection a list of
         * [Feature]s. The list of features itself isn't null but it can empty and have a size of 0.
         *
         * @param features a list of features
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeatures(features: List<Feature>): FeatureCollection {
            return FeatureCollection(TYPE, null, features)
        }

        /**
         * Create a new instance of this class by giving the feature collection an array of
         * [Feature]s. The array of features itself isn't null but it can be empty and have a length
         * of 0.
         *
         * @param features an array of features
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeatures(
            features: Array<Feature>,
            bbox: BoundingBox?
        ): FeatureCollection {
            return FeatureCollection(TYPE, bbox, Arrays.asList(*features))
        }

        /**
         * Create a new instance of this class by giving the feature collection a list of
         * [Feature]s. The list of features itself isn't null but it can be empty and have a size of
         * 0.
         *
         * @param features a list of features
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeatures(
            features: List<Feature>,
            bbox: BoundingBox?
        ): FeatureCollection {
            return FeatureCollection(TYPE, bbox, features)
        }

        /**
         * Create a new instance of this class by giving the feature collection a single [Feature].
         *
         * @param feature a single feature
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeature(feature: Feature): FeatureCollection {
            val featureList = Arrays.asList(feature)
            return FeatureCollection(TYPE, null, featureList)
        }

        /**
         * Create a new instance of this class by giving the feature collection a single [Feature].
         *
         * @param feature a single feature
         * @param bbox    optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun fromFeature(
            feature: Feature,
            bbox: BoundingBox?
        ): FeatureCollection {
            val featureList = Arrays.asList(feature)
            return FeatureCollection(TYPE, bbox, featureList)
        }

        /**
         * Gson type adapter for parsing Gson to this class.
         *
         * @param gson the built [Gson] object
         * @return the TYPE adapter for this class
         * @since 3.0.0
         */
        @kotlin.jvm.JvmStatic
        fun typeAdapter(gson: Gson): TypeAdapter<FeatureCollection> {
            return GsonTypeAdapter(gson)
        }
    }
}