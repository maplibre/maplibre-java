package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.MultiPolygon.Companion.fromPolygon
import com.mapbox.geojson.MultiPolygon.Companion.fromPolygons
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.geojson.Polygon.Companion.fromOuterInner
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException

class MultiPolygonTest : TestUtils() {
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons)
        Assert.assertNotNull(multiPolygon)
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons)
        Assert.assertNull(multiPolygon.bbox())
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons)
        compareJson(
            multiPolygon.toJson(), "{\"type\":\"MultiPolygon\","
                    + "\"coordinates\":[[[[1,2],[2,3],[3,4],[1,2]]],[[[1,2],[2,3],[3,4],[1,2]]]]}"
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons, bbox)
        Assert.assertNotNull(multiPolygon.bbox())
        Assert.assertEquals(1.0, multiPolygon.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, multiPolygon.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, multiPolygon.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, multiPolygon.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun passingInSinglePolygon_doesHandleCorrectly() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(3.0, 4.0))
        val pointsList: MutableList<List<Point>> = ArrayList()
        pointsList.add(points)
        val geometry = fromLngLats(pointsList)
        val multiPolygon = fromPolygon(geometry)
        Assert.assertNotNull(multiPolygon)
        Assert.assertEquals(1, multiPolygon.polygons().size.toLong())
        Assert.assertEquals(
            2.0,
            multiPolygon.polygons()[0].coordinates()[0][0].latitude(),
            DELTA
        )
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons, bbox)
        compareJson(
            multiPolygon.toJson(), "{\"type\":\"MultiPolygon\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"coordinates\":[[[[1,2],[2,3],[3,4],[1,2]]],[[[1,2],[2,3],[3,4],[1,2]]]]}"
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
        val polygons: MutableList<Polygon> = ArrayList()
        polygons.add(fromOuterInner(outer))
        polygons.add(fromOuterInner(outer))
        val multiPolygon = fromPolygons(polygons, bbox)
        val bytes: ByteArray = serialize(multiPolygon)
        Assert.assertEquals(
            multiPolygon,
            deserialize(bytes, MultiPolygon::class.java)
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "{\"type\":\"MultiPolygon\",\"coordinates\": " +
                "    [[[[102, 2], [103, 2], [103, 3], [102, 3], [102, 2]]]," +
                "     [[[100, 0], [101, 0], [101, 1], [100, 1], [100, 0]]," +
                "      [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}"
        val geo = MultiPolygon.fromJson(json)
        Assert.assertEquals(geo.type(), "MultiPolygon")
        Assert.assertEquals(
            geo.coordinates()!![0][0][0].longitude(),
            102.0,
            DELTA
        )
        Assert.assertEquals(geo.coordinates()!![0][0][0].latitude(), 2.0, DELTA)
        Assert.assertFalse(geo.coordinates()!![0][0][0].hasAltitude())
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = "{\"type\":\"MultiPolygon\",\"coordinates\": " +
                "    [[[[102, 2], [103, 2], [103, 3], [102, 3], [102, 2]]]," +
                "     [[[100, 0], [101, 0], [101, 1], [100, 1], [100, 0]]," +
                "      [[100.2, 0.2], [100.2, 0.8], [100.8, 0.8], [100.8, 0.2], [100.2, 0.2]]]]}"
        val geo = MultiPolygon.fromJson(json)
        compareJson(json, geo.toJson())
    }

    @Test
    @Throws(Exception::class)
    fun fromJson_coordinatesPresent() {
        thrown.expect(NullPointerException::class.java)
        MultiPolygon.fromJson("{\"type\":\"MultiPolygon\",\"coordinates\":null}")
    }
}