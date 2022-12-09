package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Type Adapter to serialize/deserialize Poinr into/from for double array.
 *
 * @since 4.6.0
 */
@Keep
internal class ListOfDoublesCoordinatesTypeAdapter : BaseCoordinatesTypeAdapter<List<Double>?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: List<Double>?) {
        writePointList(out, value)
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): List<Double> {
        return readPointList(`in`)
    }

    
}