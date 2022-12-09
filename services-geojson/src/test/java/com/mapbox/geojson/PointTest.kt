package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class PointTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val point = fromLngLat(1.0, 2.0)
        Assert.assertNotNull(point)
    }

    @Test
    @Throws(Exception::class)
    fun hasAltitude_returnsFalseWhenAltitudeNotPresent() {
        val point = fromLngLat(1.0, 2.0)
        Assert.assertFalse(point.hasAltitude())
    }

    @Test
    @Throws(Exception::class)
    fun hasAltitude_returnsTrueWhenAltitudeIsPresent() {
        val point = fromLngLat(1.0, 2.0, 5.0)
        Assert.assertTrue(point.hasAltitude())
    }

    @Test
    @Throws(Exception::class)
    fun altitude_doesReturnCorrectValueFromDoubleArray() {
        val coords = doubleArrayOf(1.0, 2.0, 5.0)
        val point = fromLngLat(coords)
        Assert.assertEquals(5.0, point!!.altitude(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun point_isNullWithWrongLengthDoubleArray() {
        val coords = doubleArrayOf(1.0)
        val point = fromLngLat(coords)
        Assert.assertNull(point)
    }

    @Test
    @Throws(Exception::class)
    fun longitude_doesReturnCorrectValue() {
        val point = fromLngLat(1.0, 2.0, 5.0)
        Assert.assertEquals(1.0, point.longitude(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun latitude_doesReturnCorrectValue() {
        val point = fromLngLat(1.0, 2.0, 5.0)
        Assert.assertEquals(2.0, point.latitude(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val point = fromLngLat(1.0, 2.0)
        Assert.assertNull(point.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenNotPresent() {
        val point = fromLngLat(1.0, 2.0)
        compareJson(
            point.toJson(),
            "{\"type\":\"Point\",\"coordinates\":[1.0, 2.0]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString.fromLngLats(points, bbox)
        Assert.assertNotNull(lineString.bbox())
        Assert.assertEquals(1.0, lineString.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, lineString.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, lineString.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, lineString.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val point = fromLngLat(2.0, 2.0, bbox)
        compareJson(
            point.toJson(), "{\"coordinates\": [2,2],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesDeserializeWhenPresent() {
        val point = Point.fromJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
        Assert.assertNotNull(point)
        Assert.assertNotNull(point.bbox())
        Assert.assertEquals(1.0, point.bbox()!!.southwest().longitude(), DELTA)
        Assert.assertEquals(2.0, point.bbox()!!.southwest().latitude(), DELTA)
        Assert.assertEquals(3.0, point.bbox()!!.northeast().longitude(), DELTA)
        Assert.assertEquals(4.0, point.bbox()!!.northeast().latitude(), DELTA)
        Assert.assertNotNull(point.coordinates())
        Assert.assertEquals(2.0, point.longitude(), DELTA)
        Assert.assertEquals(3.0, point.latitude(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun testSerializable() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString.fromLngLats(points, bbox)
        val bytes: ByteArray = serialize(lineString)
        Assert.assertEquals(
            lineString,
            deserialize(bytes, LineString::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{ \"type\": \"Point\", \"coordinates\": [ 100, 0] }"
        val geo = Point.fromJson(json)
        Assert.assertEquals(geo.type(), "Point")
        Assert.assertEquals(geo.longitude(), 100.0, DELTA)
        Assert.assertEquals(geo.latitude(), 0.0, DELTA)
        Assert.assertEquals(geo.altitude(), Double.NaN, DELTA)
        Assert.assertEquals(geo.coordinates()[0], 100.0, DELTA)
        Assert.assertEquals(geo.coordinates()[1], 0.0, DELTA)
        Assert.assertEquals(geo.coordinates().size.toLong(), 2)
        Assert.assertFalse(geo.hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{ \"type\": \"Point\", \"coordinates\": [ 100, 0] }"
        val geo = Point.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        Point.fromJson("{\"type\":\"Point\",\"coordinates\":null}")
    }
}