package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.exception.GeoJsonException
import com.mapbox.geojson.gson.BoundingBoxTypeAdapter
import java.io.IOException

/**
 * Base class for converting `Geometry` instances to JSON and
 * JSON to instances of `Geometry`.
 *
 * @param <G> Geometry
 * @param <T> Type of coordinates
 * @since 4.6.0
</T></G> */
@Keep
internal abstract class BaseGeometryTypeAdapter<G, T>(
    private val gson: Gson,
    private val coordinatesAdapter: TypeAdapter<T>
) : TypeAdapter<G>() {
    @Volatile
    private var stringAdapter: TypeAdapter<String?>? = null

    @Volatile
    private var boundingBoxAdapter: TypeAdapter<BoundingBox>?

    init {
        boundingBoxAdapter = BoundingBoxTypeAdapter()
    }

    @Throws(IOException::class)
    fun writeCoordinateContainer(jsonWriter: JsonWriter, obj: CoordinateContainer<T>?) {
        if (obj == null) {
            jsonWriter.nullValue()
            return
        }
        jsonWriter.beginObject()
        jsonWriter.name("type")

        var stringAdapter = stringAdapter
        if (stringAdapter == null) {
            stringAdapter = gson.getAdapter(String::class.java)
            this.stringAdapter = stringAdapter
        }
        stringAdapter!!.write(jsonWriter, obj.type())
        jsonWriter.name("bbox")
        if (obj.bbox() == null) {
            jsonWriter.nullValue()
        } else {
            var boundingBoxAdapter = boundingBoxAdapter
            if (boundingBoxAdapter == null) {
                boundingBoxAdapter = gson.getAdapter(BoundingBox::class.java)
                this.boundingBoxAdapter = boundingBoxAdapter
            }
            boundingBoxAdapter!!.write(jsonWriter, obj.bbox())
        }
        jsonWriter.name("coordinates")
        if (obj.coordinates() == null) {
            jsonWriter.nullValue()
        } else {
            val coordinatesAdapter = coordinatesAdapter
                ?: throw GeoJsonException("Coordinates type adapter is null")
            coordinatesAdapter.write(jsonWriter, obj.coordinates())
        }
        jsonWriter.endObject()
    }

    @Throws(IOException::class)
    fun readCoordinateContainer(jsonReader: JsonReader): CoordinateContainer<T>? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        jsonReader.beginObject()
        var type: String? = null
        var bbox: BoundingBox? = null
        var coordinates: T? = null
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
                "coordinates" -> {
                    val coordinatesAdapter = coordinatesAdapter
                        ?: throw GeoJsonException("Coordinates type adapter is null")
                    coordinates = coordinatesAdapter.read(jsonReader)
                }
                else -> jsonReader.skipValue()
            }
        }
        jsonReader.endObject()
        return createCoordinateContainer(type, bbox, coordinates)
    }

    abstract fun createCoordinateContainer(
        type: String?,
        bbox: BoundingBox?,
        coordinates: T?
    ): CoordinateContainer<T>?
}