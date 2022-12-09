package com.mapbox.geojson.gson

import androidx.annotation.Keep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.exception.GeoJsonException
import com.mapbox.geojson.shifter.CoordinateShifterManager
import com.mapbox.geojson.utils.GeoJsonUtils
import java.io.IOException

/**
 * Adapter to read and write coordinates for BoundingBox class.
 *
 * @since 4.6.0
 */
@Keep
class BoundingBoxTypeAdapter : TypeAdapter<BoundingBox>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: BoundingBox?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginArray()

        // Southwest
        var point = value.southwest()
        var unshiftedCoordinates =
            CoordinateShifterManager.getCoordinateShifter().unshiftPoint(point)
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[0]!!))
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[1]!!))
        if (point.hasAltitude()) {
            out.value(unshiftedCoordinates[2])
        }

        // Northeast
        point = value.northeast()
        unshiftedCoordinates = CoordinateShifterManager.getCoordinateShifter().unshiftPoint(point)
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[0]!!))
        out.value(GeoJsonUtils.trim(unshiftedCoordinates[1]!!))
        if (point.hasAltitude()) {
            out.value(unshiftedCoordinates[2])
        }
        out.endArray()
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): BoundingBox? {
        val rawCoordinates: MutableList<Double> = ArrayList()
        `in`.beginArray()
        while (`in`.hasNext()) {
            rawCoordinates.add(`in`.nextDouble())
        }
        `in`.endArray()
        if (rawCoordinates.size == 6) {
            return fromLngLats(
                rawCoordinates[0],
                rawCoordinates[1],
                rawCoordinates[2],
                rawCoordinates[3],
                rawCoordinates[4],
                rawCoordinates[5]
            )
        }
        return if (rawCoordinates.size == 4) {
            fromLngLats(
                rawCoordinates[0],
                rawCoordinates[1],
                rawCoordinates[2],
                rawCoordinates[3]
            )
        } else {
            throw GeoJsonException(
                "The value of the bbox member MUST be an array of length 2*n where"
                        + " n is the number of dimensions represented in the contained geometries,"
                        + "with all axes of the most southwesterly point followed "
                        + " by all axes of the more northeasterly point. The "
                        + "axes order of a bbox follows the axes order of geometries."
            )
        }
    }
}