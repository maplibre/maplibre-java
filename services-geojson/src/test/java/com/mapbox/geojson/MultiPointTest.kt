package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class MultiPointTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val multiPoint = MultiPoint.fromLngLats(points)
        Assert.assertNotNull(multiPoint)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val multiPoint = MultiPoint.fromLngLats(points)
        Assert.assertNull(multiPoint.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val multiPoint = MultiPoint.fromLngLats(points)
        compareJson(
            multiPoint.toJson(),
            "{\"coordinates\":[[1,2],[2,3]],\"type\":\"MultiPoint\"}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint.fromLngLats(points, bbox)
        Assert.assertNotNull(multiPoint.bbox())
        Assert.assertEquals(1.0, multiPoint.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, multiPoint.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, multiPoint.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, multiPoint.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint.fromLngLats(points, bbox)
        compareJson(
            multiPoint.toJson(),
            "{\"coordinates\":[[1,2],[2,3]],\"type\":\"MultiPoint\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSerializable() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint.fromLngLats(points, bbox)
        val bytes: ByteArray = serialize(multiPoint)
        Assert.assertEquals(
            multiPoint,
            deserialize(bytes, MultiPoint::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{ \"type\": \"MultiPoint\"," +
                "\"coordinates\": [ [100, 0], [101, 1] ] } "
        val geo = MultiPoint.fromJson(json)
        Assert.assertEquals(geo.type(), "MultiPoint")
        Assert.assertEquals(geo.coordinates()[0].longitude(), 100.0, DELTA)
        Assert.assertEquals(geo.coordinates()[0].latitude(), 0.0, DELTA)
        Assert.assertEquals(geo.coordinates()[1].longitude(), 101.0, DELTA)
        Assert.assertEquals(geo.coordinates()[1].latitude(), 1.0, DELTA)
        Assert.assertFalse(geo.coordinates()[0].hasAltitude())
        Assert.assertEquals(Double.NaN, geo.coordinates()[0].altitude(), DELTA)
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{ \"type\": \"MultiPoint\"," +
                "\"coordinates\": [ [100, 0], [101, 1] ] } "
        val geo = MultiPoint.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        MultiPoint.fromJson("{\"type\":\"MultiPoint\",\"coordinates\":null}")
    }

}