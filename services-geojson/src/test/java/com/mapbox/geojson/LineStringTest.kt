package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.LineString
import com.mapbox.geojson.LineString.Companion.fromLngLats
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class LineStringTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        Assert.assertNotNull(lineString)
    }

    @Test
    @Throws(Exception::class)
    fun fromLngLats_generatedFromMultipoint() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(4.0, 8.0))
        val multiPoint = MultiPoint.fromLngLats(points)
        val lineString = fromLngLats(multiPoint)
        Assert.assertEquals("_gayB_c`|@_wemJ_kbvD", lineString.toPolyline(PRECISION_6))
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        Assert.assertNull(lineString.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        compareJson(
            lineString.toJson(),
            "{\"coordinates\":[[1,1],[2,2],[3,3]],\"type\":\"LineString\"}"
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
        Assert.assertEquals(1.0, lineString.bbox()!!.west(), TestUtils.Companion.DELTA)
        Assert.assertEquals(2.0, lineString.bbox()!!.south(), TestUtils.Companion.DELTA)
        Assert.assertEquals(3.0, lineString.bbox()!!.east(), TestUtils.Companion.DELTA)
        Assert.assertEquals(4.0, lineString.bbox()!!.north(), TestUtils.Companion.DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString.fromLngLats(points, bbox)
        val lineStringJson = lineString.toJson()
        compareJson(
            "{\"coordinates\":[[1,1],[2,2],[3,3]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}",
            lineStringJson
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesDeserializeWhenPresent() {
        val lineString = LineString.fromJson(
            "{\"coordinates\":[[1,2],[2,3],[3,4]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
        Assert.assertNotNull(lineString)
        Assert.assertNotNull(lineString.bbox())
        Assert.assertEquals(
            1.0,
            lineString.bbox()!!.southwest().longitude(),
            TestUtils.Companion.DELTA
        )
        Assert.assertEquals(
            2.0,
            lineString.bbox()!!.southwest().latitude(),
            TestUtils.Companion.DELTA
        )
        Assert.assertEquals(
            3.0,
            lineString.bbox()!!.northeast().longitude(),
            TestUtils.Companion.DELTA
        )
        Assert.assertEquals(
            4.0,
            lineString.bbox()!!.northeast().latitude(),
            TestUtils.Companion.DELTA
        )
        Assert.assertNotNull(lineString.coordinates())
        Assert.assertEquals(1.0, lineString.coordinates()[0].longitude(), TestUtils.Companion.DELTA)
        Assert.assertEquals(2.0, lineString.coordinates()[0].latitude(), TestUtils.Companion.DELTA)
        Assert.assertEquals(2.0, lineString.coordinates()[1].longitude(), TestUtils.Companion.DELTA)
        Assert.assertEquals(3.0, lineString.coordinates()[1].latitude(), TestUtils.Companion.DELTA)
        Assert.assertEquals(3.0, lineString.coordinates()[2].longitude(), TestUtils.Companion.DELTA)
        Assert.assertEquals(4.0, lineString.coordinates()[2].latitude(), TestUtils.Companion.DELTA)
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
        val bytes: ByteArray = TestUtils.Companion.serialize<LineString>(lineString)
        Assert.assertEquals(
            lineString,
            TestUtils.Companion.deserialize<LineString>(bytes, LineString::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{\"type\": \"LineString\"," +
                "  \"coordinates\": [[ 100, 0], [101, 1]]} "
        val geo = LineString.fromJson(json)
        Assert.assertEquals(geo.type(), "LineString")
        Assert.assertEquals(geo.coordinates()[0].longitude(), 100.0, 0.0)
        Assert.assertEquals(geo.coordinates()[0].latitude(), 0.0, 0.0)
        Assert.assertFalse(geo.coordinates()[0].hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{\"type\": \"LineString\"," +
                "  \"coordinates\": [[ 100, 0], [101, 1]]} "
        val geo = LineString.fromJson(json)
        val geoJsonString = geo.toJson()
        compareJson(geoJsonString, json)
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        LineString.fromJson("{\"type\":\"LineString\",\"coordinates\":null}")
    }

    companion object {
        private const val SAMPLE_LINESTRING_FIXTURE = "sample-linestring.json"
        private const val PRECISION_6 = 6
        private const val PRECISION_5 = 5
    }
}