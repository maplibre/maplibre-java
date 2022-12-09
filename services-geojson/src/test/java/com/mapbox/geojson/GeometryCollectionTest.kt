package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.GeometryCollection.Companion.fromGeometries
import com.mapbox.geojson.GeometryCollection.Companion.fromGeometry
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.util.*

class GeometryCollectionTest : TestUtils() {
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val geometryCollection = fromGeometries(geometries)
        Assert.assertNotNull(geometryCollection)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val geometryCollection = fromGeometries(geometries)
        Assert.assertNull(geometryCollection.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val geometryCollection = fromGeometries(geometries)
        compareJson(
            geometryCollection.toJson(),
            "{\"type\":\"GeometryCollection\",\"geometries\":[" + "{\"type\":\"Point\","
                    + "\"coordinates\":[1.0,2.0]},{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]}]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = fromGeometries(geometries, bbox)
        Assert.assertNotNull(geometryCollection.bbox())
        Assert.assertEquals(1.0, geometryCollection.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, geometryCollection.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, geometryCollection.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, geometryCollection.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun passingInSingleGeometry_doesHandleCorrectly() {
        val geometry = fromLngLat(1.0, 2.0)
        val collection = fromGeometry(geometry)
        Assert.assertNotNull(collection)
        Assert.assertEquals(1, collection.geometries().size.toLong())
        Assert.assertEquals(
            2.0,
            (collection.geometries()[0] as Point).latitude(),
            DELTA
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = fromGeometries(geometries, bbox)
        compareJson(
            geometryCollection.toJson(),
            "{\"type\":\"GeometryCollection\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"geometries\":[{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},"
                    + "{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]}]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSerializable() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = fromGeometries(geometries, bbox)
        val bytes: ByteArray = serialize(geometryCollection)
        Assert.assertEquals(
            geometryCollection,
            deserialize(
                bytes,
                GeometryCollection::class.java
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = "    { \"type\": \"GeometryCollection\"," +
                "            \"bbox\": [120, 40, -120, -40]," +
                "      \"geometries\": [" +
                "      { \"type\": \"Point\"," +
                "              \"bbox\": [110, 30, -110, -30]," +
                "        \"coordinates\": [100, 0]}," +
                "      { \"type\": \"LineString\"," +
                "              \"bbox\": [110, 30, -110, -30]," +
                "        \"coordinates\": [[101, 0], [102, 1]]}]}"
        val geo = GeometryCollection.fromJson(json)
        Assert.assertEquals(geo.type(), "GeometryCollection")
        Assert.assertEquals(geo.geometries()[0].type(), "Point")
        Assert.assertEquals(geo.geometries()[1].type(), "LineString")
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val jsonOriginal = "    { \"type\": \"GeometryCollection\"," +
                "            \"bbox\": [-120, -40, 120, 40]," +
                "      \"geometries\": [" +
                "      { \"type\": \"Point\"," +
                "              \"bbox\": [-110, -30, 110, 30]," +
                "        \"coordinates\": [100, 0]}," +
                "      { \"type\": \"LineString\"," +
                "              \"bbox\": [-110, -30, 110, 30]," +
                "        \"coordinates\": [[101, 0], [102, 1]]}]}"
        val geometries: MutableList<Geometry> = ArrayList(2)
        geometries.add(
            fromLngLat(
                100.0, 0.0,
                fromLngLats(-110.0, -30.0, 110.0, 30.0)
            )
        )
        geometries.add(
            LineString.fromLngLats(
                listOf(
                    fromLngLat(101.0, 0.0),
                    fromLngLat(102.0, 1.0)
                ),
                fromLngLats(-110.0, -30.0, 110.0, 30.0)
            )
        )
        val geometryCollection = fromGeometries(
            geometries,
            fromLngLats(-120.0, -40.0, 120.0, 40.0)
        )
        val jsonString = geometryCollection.toJson()
        compareJson(jsonOriginal, jsonString)
    }
    
}