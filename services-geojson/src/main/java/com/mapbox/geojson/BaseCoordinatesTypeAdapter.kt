package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.TypeAdapter
import java.io.IOException
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonReader
import com.mapbox.geojson.exception.GeoJsonException
import com.mapbox.geojson.shifter.CoordinateShifterManager
import com.mapbox.geojson.utils.GeoJsonUtils
import com.google.gson.stream.JsonToken
import java.lang.NullPointerException
import java.util.ArrayList

/**
 * Base class for converting `T` instance of coordinates to JSON and
 * JSON to instance of `T`.
 *
 * @param <T> Type of coordinates
 * @since 4.6.0
</T> */
@Keep
abstract class BaseCoordinatesTypeAdapter<T> : TypeAdapter<T>() {
    @Throws(IOException::class)
    protected fun writePoint(out: JsonWriter, point: Point?) {
        if (point == null) {
            return
        }
        writePointList(out, point.coordinates())
    }

    @Throws(IOException::class)
    protected fun readPoint(inReader: JsonReader): Point {
        val coordinates = readPointList(inReader)
        if (coordinates.size > 1) {
            return Point("Point", null, coordinates)
        }
        throw GeoJsonException(" Point coordinates should be non-null double array")
    }

    @Throws(IOException::class)
    protected fun writePointList(out: JsonWriter, value: List<Double>?) {
        if (value == null) {
            return
        }
        out.beginArray()

        // Unshift coordinates
        val unshiftedCoordinates =
            CoordinateShifterManager.getCoordinateShifter().unshiftPoint(value)
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[0]))
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[1]))

        // Includes altitude
        if (value.size > 2) {
            out.value(unshiftedCoordinates[2])
        }
        out.endArray()
    }

    @Throws(IOException::class)
    protected fun readPointList(inReader: JsonReader): List<Double> {
        if (inReader.peek() == JsonToken.NULL) {
            throw NullPointerException()
        }
        val coordinates: MutableList<Double> = ArrayList()
        inReader.beginArray()
        while (inReader.hasNext()) {
            coordinates.add(inReader.nextDouble())
        }
        inReader.endArray()
        return if (coordinates.size > 2) {
            CoordinateShifterManager.getCoordinateShifter()
                .shiftLonLatAlt(coordinates[0], coordinates[1], coordinates[2])
        } else CoordinateShifterManager.getCoordinateShifter()
            .shiftLonLat(coordinates[0], coordinates[1])
    }
}