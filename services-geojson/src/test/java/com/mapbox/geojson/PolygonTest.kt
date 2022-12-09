package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.geojson.Polygon.Companion.fromOuterInner
import com.mapbox.geojson.exception.GeoJsonException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class PolygonTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val polygon = fromOuterInner(outer)
        Assert.assertNotNull(polygon)
    }

    @Test
    @Throws(Exception::class)
    fun fromLngLats_tripleDoubleArray() {
        val coordinates = arrayOf(
            arrayOf(
                doubleArrayOf(100.0, 0.0),
                doubleArrayOf(101.0, 0.0),
                doubleArrayOf(101.0, 1.0),
                doubleArrayOf(100.0, 1.0),
                doubleArrayOf(100.0, 0.0)
            )
        )
        val polygon = fromLngLats(coordinates)
        Assert.assertEquals(0, polygon.inner().size.toLong())
        Assert.assertEquals(fromLngLat(100.0, 0.0), polygon.coordinates()[0][0])
    }

    @Test
    @Throws(Exception::class)
    fun fromOuterInner_throwsNotLinearRingException() {
        thrown.expect(GeoJsonException::class.java)
        thrown.expectMessage("LinearRings need to be made up of 4 or more coordinates.")
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(10.0, 2.0))
        points.add(fromLngLat(5.0, 2.0))
        points.add(fromLngLat(3.0, 2.0))
        val lineString = LineString.fromLngLats(points)
        @Suppress("UNUSED_VARIABLE") val polygon = fromOuterInner(lineString)
    }

    @Test
    @Throws(Exception::class)
    fun fromOuterInner_throwsNotConnectedLinearRingException() {
        thrown.expect(GeoJsonException::class.java)
        thrown.expectMessage("LinearRings require first and last coordinate to be identical.")
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(10.0, 2.0))
        points.add(fromLngLat(5.0, 2.0))
        points.add(fromLngLat(3.0, 2.0))
        points.add(fromLngLat(5.0, 2.0))
        val lineString = LineString.fromLngLats(points)
        @Suppress("UNUSED_VARIABLE") val polygon = fromOuterInner(lineString)
    }

    @Test
    @Throws(Exception::class)
    fun fromOuterInner_handlesSingleLineStringCorrectly() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(10.0, 2.0))
        points.add(fromLngLat(5.0, 2.0))
        points.add(fromLngLat(3.0, 2.0))
        points.add(fromLngLat(10.0, 2.0))
        val lineString = LineString.fromLngLats(points)
        val polygon = fromOuterInner(lineString)
        Assert.assertEquals(fromLngLat(10.0, 2.0), polygon.coordinates()[0][0])
    }

    @Test
    @Throws(Exception::class)
    fun fromOuterInner_handlesOuterAndInnerLineStringCorrectly() {
        val outer: MutableList<Point> = ArrayList()
        outer.add(fromLngLat(10.0, 2.0))
        outer.add(fromLngLat(5.0, 2.0))
        outer.add(fromLngLat(3.0, 2.0))
        outer.add(fromLngLat(10.0, 2.0))
        val outerLineString = LineString.fromLngLats(outer)
        val inner: MutableList<Point> = ArrayList()
        inner.add(fromLngLat(5.0, 2.0))
        inner.add(fromLngLat(2.5, 2.0))
        inner.add(fromLngLat(1.5, 2.0))
        inner.add(fromLngLat(5.0, 2.0))
        val innerLineString = LineString.fromLngLats(inner)
        val polygon = fromOuterInner(outerLineString, innerLineString)
        Assert.assertEquals(fromLngLat(10.0, 2.0), polygon.coordinates()[0][0])
        Assert.assertEquals(outerLineString, polygon.outer())
        Assert.assertEquals(1, polygon.inner().size.toLong())
        Assert.assertEquals(innerLineString, polygon.inner()[0])
    }

    @Test
    @Throws(Exception::class)
    fun fromOuterInner_withABoundingBox() {
        val outer: MutableList<Point> = ArrayList()
        outer.add(fromLngLat(10.0, 2.0))
        outer.add(fromLngLat(5.0, 2.0))
        outer.add(fromLngLat(3.0, 2.0))
        outer.add(fromLngLat(10.0, 2.0))
        val outerLineString = LineString.fromLngLats(outer)
        val inner: MutableList<Point> = ArrayList()
        inner.add(fromLngLat(5.0, 2.0))
        inner.add(fromLngLat(2.5, 2.0))
        inner.add(fromLngLat(1.5, 2.0))
        inner.add(fromLngLat(5.0, 2.0))
        val innerLineString = LineString.fromLngLats(inner)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val polygon = fromOuterInner(outerLineString, bbox, innerLineString)
        Assert.assertEquals(bbox, polygon.bbox())
        Assert.assertEquals(outerLineString, polygon.outer())
        Assert.assertEquals(1, polygon.inner().size.toLong())
        Assert.assertEquals(innerLineString, polygon.inner()[0])
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val inner: MutableList<LineString> = ArrayList()
        inner.add(LineString.fromLngLats(points))
        inner.add(LineString.fromLngLats(points))
        val polygon = fromOuterInner(outer, inner)
        Assert.assertNull(polygon.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val inner: MutableList<LineString> = ArrayList()
        inner.add(LineString.fromLngLats(points))
        inner.add(LineString.fromLngLats(points))
        val polygon = fromOuterInner(outer, inner)
        compareJson(
            polygon.toJson(), "{\"type\":\"Polygon\",\"coordinates\":"
                    + "[[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]]]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val inner: MutableList<LineString> = ArrayList()
        inner.add(LineString.fromLngLats(points))
        inner.add(LineString.fromLngLats(points))
        val polygon = fromOuterInner(outer, bbox, inner)
        Assert.assertNotNull(polygon.bbox())
        Assert.assertEquals(1.0, polygon.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, polygon.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, polygon.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, polygon.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val inner: MutableList<LineString> = ArrayList()
        inner.add(LineString.fromLngLats(points))
        inner.add(LineString.fromLngLats(points))
        val polygon = fromOuterInner(outer, bbox, inner)
        compareJson(
            polygon.toJson(), "{\"type\":\"Polygon\",\"bbox\":[1.0,2.0,3.0,4.0],\"coordinates\":"
                    + "[[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]],[[1,2],[2,3],[3,4],[1,2]]]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSerializable() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        points.add(fromLngLat(3.0, 4.0))
        points.add(fromLngLat(1.0, 2.0))
        val outer = LineString.fromLngLats(points)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val inner: MutableList<LineString> = ArrayList()
        inner.add(LineString.fromLngLats(points))
        inner.add(LineString.fromLngLats(points))
        val polygon = fromOuterInner(outer, bbox, inner)
        val bytes: ByteArray = serialize(polygon)
        Assert.assertEquals(
            polygon,
            deserialize(bytes, Polygon::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]]]}"
        val geo = Polygon.fromJson(json)
        Assert.assertEquals("Polygon", geo.type())
        Assert.assertEquals(100.0, geo.coordinates()[0][0].longitude(), DELTA)
        Assert.assertEquals(0.0, geo.coordinates()[0][0].latitude(), DELTA)
        Assert.assertFalse(geo.coordinates()[0][0].hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun fromJsonHoles() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]], " +
                " [[100.8, 0.8],[100.8, 0.2],[100.2, 0.2],[100.2, 0.8],[100.8, 0.8]]]}"
        val geo = Polygon.fromJson(json)
        Assert.assertEquals("Polygon", geo.type())
        Assert.assertEquals(100.0, geo.coordinates()[0][0].longitude(), DELTA)
        Assert.assertEquals(0.0, geo.coordinates()[0][0].latitude(), DELTA)
        Assert.assertEquals(2, geo.coordinates().size.toLong())
        Assert.assertEquals(100.8, geo.coordinates()[1][0].longitude(), DELTA)
        Assert.assertEquals(0.8, geo.coordinates()[1][0].latitude(), DELTA)
        Assert.assertFalse(geo.coordinates()[0][0].hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]]]}"
        val geo = Polygon.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(IOException::class)
    fun toJsonHoles() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]], " +
                " [[100.8, 0.8],[100.8, 0.2],[100.2, 0.2],[100.2, 0.8],[100.8, 0.8]]]}"
        val geo = Polygon.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":null}")
    }
}