package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.exception.GeoJsonException
import java.io.IOException

/**
 * Type Adapter to serialize/deserialize List&lt;List&lt;List&lt;Point&gt;&gt;&gt; into/from
 * four dimentional double array.
 *
 * @since 4.6.0
 */
@Keep
internal class ListofListofListOfPointCoordinatesTypeAdapter :
    BaseCoordinatesTypeAdapter<List<List<List<Point>>>?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, points: List<List<List<Point>>>?) {
        if (points == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (listOfListOfPoints in points) {
            out.beginArray()
            for (listOfPoints in listOfListOfPoints) {
                out.beginArray()
                for (point in listOfPoints) {
                    writePoint(out, point)
                }
                out.endArray()
            }
            out.endArray()
        }
        out.endArray()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): List<List<List<Point>>> {
        if (`in`.peek() == JsonToken.NULL) {
            throw NullPointerException()
        }
        if (`in`.peek() == JsonToken.BEGIN_ARRAY) {
            `in`.beginArray()
            val listOfListOflistOfPoints: MutableList<List<List<Point>>> = ArrayList()
            while (`in`.peek() == JsonToken.BEGIN_ARRAY) {
                `in`.beginArray()
                val listOfListOfPoints: MutableList<List<Point>> = ArrayList()
                while (`in`.peek() == JsonToken.BEGIN_ARRAY) {
                    `in`.beginArray()
                    val listOfPoints: MutableList<Point> = ArrayList()
                    while (`in`.peek() == JsonToken.BEGIN_ARRAY) {
                        listOfPoints.add(readPoint(`in`))
                    }
                    `in`.endArray()
                    listOfListOfPoints.add(listOfPoints)
                }
                `in`.endArray()
                listOfListOflistOfPoints.add(listOfListOfPoints)
            }
            `in`.endArray()
            return listOfListOflistOfPoints
        }
        throw GeoJsonException("coordinates should be array of array of array of double")
    }
}