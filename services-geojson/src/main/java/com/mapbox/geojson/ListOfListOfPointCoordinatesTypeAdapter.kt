package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.exception.GeoJsonException
import java.io.IOException

/**
 * Type Adapter to serialize/deserialize ist &lt;List&lt;Point&gt;&gt;
 * into/from three dimentional double array.
 *
 * @since 4.6.0
 */
@Keep
internal class ListOfListOfPointCoordinatesTypeAdapter :
    BaseCoordinatesTypeAdapter<List<List<Point>>?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, points: List<List<Point>>?) {
        if (points == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (listOfPoints in points) {
            out.beginArray()
            for (point in listOfPoints) {
                writePoint(out, point)
            }
            out.endArray()
        }
        out.endArray()
    }

    @Throws(IOException::class)
    override fun read(inReader: JsonReader): List<List<Point>> {
        if (inReader.peek() == JsonToken.NULL) {
            throw NullPointerException()
        }
        if (inReader.peek() == JsonToken.BEGIN_ARRAY) {
            inReader.beginArray()
            val points: MutableList<List<Point>> = ArrayList()
            while (inReader.peek() == JsonToken.BEGIN_ARRAY) {
                inReader.beginArray()
                val listOfPoints: MutableList<Point> = ArrayList()
                while (inReader.peek() == JsonToken.BEGIN_ARRAY) {
                    listOfPoints.add(readPoint(inReader))
                }
                inReader.endArray()
                points.add(listOfPoints)
            }
            inReader.endArray()
            return points
        }
        throw GeoJsonException("coordinates should be array of array of array of double")
    }
}