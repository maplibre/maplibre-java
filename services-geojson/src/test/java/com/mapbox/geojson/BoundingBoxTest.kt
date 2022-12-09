package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromPoints
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Test

class BoundingBoxTest : TestUtils() {
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val southwest = fromLngLat(2.0, 2.0)
        val northeast = fromLngLat(4.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertNotNull(boundingBox)
    }

    @Test
    @Throws(Exception::class)
    fun southWest_doesReturnMostSouthwestCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertTrue(southwest == boundingBox.southwest())
    }

    @Test
    @Throws(Exception::class)
    fun northEast_doesReturnMostNortheastCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertTrue(northeast == boundingBox.northeast())
    }

    @Test
    @Throws(Exception::class)
    fun west_doesReturnMostWestCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertEquals(1.0, boundingBox.west(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun south_doesReturnMostSouthCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertEquals(2.0, boundingBox.south(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun east_doesReturnMostEastCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertEquals(3.0, boundingBox.east(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun north_doesReturnMostNorthCoordinate() {
        val southwest = fromLngLat(1.0, 2.0)
        val northeast = fromLngLat(3.0, 4.0)
        val boundingBox = fromPoints(southwest, northeast)
        Assert.assertEquals(4.0, boundingBox.north(), DELTA)
    }
}