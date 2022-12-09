package com.mapbox.geojson.gson

import androidx.annotation.Keep
import com.google.gson.GsonBuilder
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryAdapterFactory.Companion.create

/**
 * This is a utility class that helps create a Geometry instance from a JSON string.
 * @since 4.0.0
 */
@Keep
object GeometryGeoJson {
    /**
     * Create a new instance of Geometry class by passing in a formatted valid JSON String.
     *
     * @param json a formatted valid JSON string defining a GeoJson Geometry
     * @return a new instance of Geometry class defined by the values passed inside
     * this static factory method
     * @since 4.0.0
     */
    @JvmStatic
    fun fromJson(json: String): Geometry {
        val gson = GsonBuilder()
        gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create())
        gson.registerTypeAdapterFactory(create())
        return gson.create().fromJson(json, Geometry::class.java)
    }
}