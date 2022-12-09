package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.exception.GeoJsonException
import java.io.IOException

/**
 * Type Adapter to serialize/deserialize List&lt;Point&gt; into/from two dimentional double array.
 *
 * @since 4.6.0
 */
@Keep
internal class ListOfPointCoordinatesTypeAdapter : BaseCoordinatesTypeAdapter<List<Point>?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, points: List<Point>?) {
        if (points == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (point in points) {
            writePoint(out, point)
        }
        out.endArray()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): List<Point> {
        if (`in`.peek() == JsonToken.NULL) {
            throw NullPointerException()
        }
        if (`in`.peek() == JsonToken.BEGIN_ARRAY) {
            val points: MutableList<Point> = ArrayList()
            `in`.beginArray()
            while (`in`.peek() == JsonToken.BEGIN_ARRAY) {
                points.add(readPoint(`in`))
            }
            `in`.endArray()
            return points
        }
        throw GeoJsonException("coordinates should be non-null array of array of double")
    }

}