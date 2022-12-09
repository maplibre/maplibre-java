package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.gson.GeometryGeoJson
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.util.*

class GeometryTest : TestUtils() {
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
        val geo = GeometryGeoJson.fromJson(json)
        Assert.assertEquals(geo.type(), "GeometryCollection")
    }

    @Test
    @Throws(IOException::class)
    fun pointFromJson() {
        val geometry = GeometryGeoJson.fromJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
        Assert.assertNotNull(geometry)
        Assert.assertNotNull(geometry.bbox())
        Assert.assertEquals(
            1.0,
            geometry.bbox()!!.southwest().longitude(),
            DELTA
        )
        Assert.assertEquals(
            2.0,
            geometry.bbox()!!.southwest().latitude(),
            DELTA
        )
        Assert.assertEquals(
            3.0,
            geometry.bbox()!!.northeast().longitude(),
            DELTA
        )
        Assert.assertEquals(
            4.0,
            geometry.bbox()!!.northeast().latitude(),
            DELTA
        )
        Assert.assertNotNull((geometry as Point).coordinates())
        Assert.assertEquals(2.0, geometry.longitude(), DELTA)
        Assert.assertEquals(3.0, geometry.latitude(), DELTA)
    }

    @Test
    @Throws(IOException::class)
    fun pointToJson() {
        val geometry: Geometry = fromLngLat(
            2.0, 3.0,
            fromLngLats(1.0, 2.0, 3.0, 4.0)
        )
        val pointStr = geometry.toJson()
        compareJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}",
            pointStr
        )
    }

    @Test
    @Throws(Exception::class)
    fun lineStringFromJson() {
        val lineString = GeometryGeoJson.fromJson(
            "{\"coordinates\":[[1,2],[2,3],[3,4]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )
        Assert.assertNotNull(lineString)
        Assert.assertNotNull(lineString.bbox())
        Assert.assertEquals(
            1.0,
            lineString.bbox()!!.southwest().longitude(),
            DELTA
        )
        Assert.assertEquals(
            2.0,
            lineString.bbox()!!.southwest().latitude(),
            DELTA
        )
        Assert.assertEquals(
            3.0,
            lineString.bbox()!!.northeast().longitude(),
            DELTA
        )
        Assert.assertEquals(
            4.0,
            lineString.bbox()!!.northeast().latitude(),
            DELTA
        )
        Assert.assertNotNull((lineString as LineString).coordinates())
        Assert.assertEquals(1.0, lineString.coordinates()[0].longitude(), DELTA)
        Assert.assertEquals(2.0, lineString.coordinates()[0].latitude(), DELTA)
        Assert.assertEquals(2.0, lineString.coordinates()[1].longitude(), DELTA)
        Assert.assertEquals(3.0, lineString.coordinates()[1].latitude(), DELTA)
        Assert.assertEquals(3.0, lineString.coordinates()[2].longitude(), DELTA)
        Assert.assertEquals(4.0, lineString.coordinates()[2].latitude(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun lineStringToJson() {
        val geometry: Geometry = LineString.fromLngLats(
            listOf(
                fromLngLat(1.0, 2.0),
                fromLngLat(2.0, 3.0),
                fromLngLat(3.0, 4.0)
            ),
            fromLngLats(1.0, 2.0, 3.0, 4.0)
        )
        val geometryJsonStr = geometry.toJson()
        val expectedJsonString = ("{\"coordinates\":[[1,2],[2,3],[3,4]],"
                + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}")
        compareJson(expectedJsonString, geometryJsonStr)
    }

}