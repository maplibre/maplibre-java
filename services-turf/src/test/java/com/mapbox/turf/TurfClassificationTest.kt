package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfClassification.nearestPoint
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class TurfClassificationTest : TestUtils() {
    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineDistanceWithGeometries() {
        val pt = Feature.fromJson(loadJsonFixture(PT)).geometry() as Point?
        val pts = FeatureCollection.fromJson(loadJsonFixture(PTS))
        val pointList: MutableList<Point> = ArrayList()
        for (feature in pts.features()!!) {
            pointList.add(feature.geometry() as Point)
        }
        val closestPt = nearestPoint(pt!!, pointList)
        Assert.assertNotNull(closestPt)
        Assert.assertEquals(closestPt.type(), "Point")
        Assert.assertEquals(closestPt.longitude(), -75.33, DELTA)
        Assert.assertEquals(closestPt.latitude(), 39.44, DELTA)
    }

    companion object {
        private const val PT = "turf-classification/pt.json"
        private const val PTS = "turf-classification/pts.json"
    }
}