package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiLineString.Companion.fromLineString
import com.mapbox.geojson.MultiLineString.Companion.fromLineStrings
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class MultiLineStringTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings)
        Assert.assertNotNull(multiLineString)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings)
        Assert.assertNull(multiLineString.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings)
        compareJson(
            multiLineString.toJson(),
            "{\"type\":\"MultiLineString\",\"coordinates\":[[[1,2],[2,3]],[[1,2],[2,3]]]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings, bbox)
        Assert.assertNotNull(multiLineString.bbox())
        Assert.assertEquals(1.0, multiLineString.bbox()!!.west(), TestUtils.Companion.DELTA)
        Assert.assertEquals(2.0, multiLineString.bbox()!!.south(), TestUtils.Companion.DELTA)
        Assert.assertEquals(3.0, multiLineString.bbox()!!.east(), TestUtils.Companion.DELTA)
        Assert.assertEquals(4.0, multiLineString.bbox()!!.north(), TestUtils.Companion.DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun passingInSingleLineString_doesHandleCorrectly() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(3.0, 4.0))
        val geometry = LineString.fromLngLats(points)
        val multiLineString = fromLineString(geometry)
        Assert.assertNotNull(multiLineString)
        Assert.assertEquals(1, multiLineString.lineStrings().size.toLong())
        Assert.assertEquals(
            2.0,
            multiLineString.lineStrings()[0].coordinates()[0].latitude(),
            TestUtils.Companion.DELTA
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings, bbox)
        compareJson(
            multiLineString.toJson(), "{\"type\":\"MultiLineString\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"coordinates\":[[[1,2],[2,3]],[[1,2],[2,3]]]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSerializable() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineStrings: MutableList<LineString> = ArrayList()
        lineStrings.add(LineString.fromLngLats(points))
        lineStrings.add(LineString.fromLngLats(points))
        val multiLineString = fromLineStrings(lineStrings, bbox)
        val bytes: ByteArray = TestUtils.Companion.serialize<MultiLineString>(multiLineString)
        Assert.assertEquals(
            multiLineString,
            TestUtils.Companion.deserialize<MultiLineString>(bytes, MultiLineString::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100, 0],[101, 1]],[[102, 2],[103, 3]]] }"
        val geo = MultiLineString.fromJson(json)
        Assert.assertEquals("MultiLineString", geo.type())
        Assert.assertEquals(geo.coordinates()[0][0].longitude(), 100.0, TestUtils.Companion.DELTA)
        Assert.assertEquals(geo.coordinates()[0][0].latitude(), 0.0, TestUtils.Companion.DELTA)
        Assert.assertFalse(geo.coordinates()[0][0].hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100, 0],[101, 1]],[[102, 2],[103, 3]]] }"
        val geo = MultiLineString.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        MultiLineString.fromJson("{\"type\":\"MultiLineString\",\"coordinates\":null}")
    }

    companion object {
        private const val SAMPLE_MULTILINESTRING = "sample-multilinestring.json"
        private const val PRECISION_6 = 6
        private const val PRECISION_5 = 5
    }
}