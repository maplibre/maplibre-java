package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfTransformation.circle
import org.junit.Ignore
import org.junit.Test

class TurfTransformationTest : TestUtils() {
    @Test
    @Ignore
    @Throws(Exception::class)
    fun name() {
        val feature = Feature.fromJson(loadJsonFixture(CIRCLE_IN))
        val polygon = circle(
            (feature.geometry() as Point?)!!,
            feature.getNumberProperty("radius")!!.toDouble()
        )
        val featureCollection = FeatureCollection.fromJson(loadJsonFixture(CIRCLE_OUT))
        compareJson(featureCollection.features()!![1].geometry()!!.toJson(), polygon.toJson())
    }

    companion object {
        private const val CIRCLE_IN = "turf-transformation/circle_in.json"
        private const val CIRCLE_OUT = "turf-transformation/circle_out.json"
    }
}