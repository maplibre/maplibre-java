package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * TypeAdapter to serialize Point as coordinates, i.e array of doubles and
 * to deserialize into Point out of array of doubles.
 *
 * @since 4.6.0
 */
@Keep
class PointAsCoordinatesTypeAdapter : BaseCoordinatesTypeAdapter<Point>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Point) {
        writePoint(out, value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Point {
        return readPoint(`in`)
    }
}