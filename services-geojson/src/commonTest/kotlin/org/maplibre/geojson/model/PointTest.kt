package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson
import kotlin.test.assertFailsWith

class PointTest {

    @Test
    fun sanity() {
        val point = Point(1.0, 2.0)
        assertNotNull(point)
    }

    @Test
    fun altitude_returnsIsOptional() {
        val point = Point(1.0, 2.0)
        assertNull(point.altitude)
    }

    @Test
    fun longitude_doesReturnCorrectValue() {
        val point = Point(1.0, 2.0, 5.0)
        assertEquals(1.0, point.longitude, DELTA)
    }

    @Test
    fun latitude_doesReturnCorrectValue() {
        val point = Point(1.0, 2.0, 5.0)
        assertEquals(2.0, point.latitude, DELTA)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val point = Point(1.0, 2.0)
        assertNull(point.boundingBox)
    }

    @Test
    fun bbox_doesSerializeWhenNotPresent() {
        val point = Point(1.0, 2.0)

        val actualPoint = Point.fromJson(point.toJson())
        val expectedPoint = Point.fromJson("{\"type\":\"Point\",\"coordinates\":[1.0, 2.0]}")
        assertEquals(expectedPoint, actualPoint)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString(points, bbox)
        assertNotNull(lineString.boundingBox)
        assertEquals(1.0, lineString.boundingBox!!.west, DELTA)
        assertEquals(2.0, lineString.boundingBox!!.south, DELTA)
        assertEquals(3.0, lineString.boundingBox!!.east, DELTA)
        assertEquals(4.0, lineString.boundingBox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val point = Point(2.0, 2.0, boundingBox = bbox)

        val actualPoint = Point.fromJson(point.toJson())
        val expectedPoint = Point.fromJson("{\"coordinates\": [2.0,2.0],"
                + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}")
        assertEquals(expectedPoint, actualPoint)
    }

    @Test
    fun bbox_doesDeserializeWhenPresent() {
        val point: Point = Point.fromJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )

        assertNotNull(point)
        assertEquals(2.0, point.longitude, DELTA)
        assertEquals(3.0, point.latitude, DELTA)
        assertNotNull(point.boundingBox)
        assertEquals(1.0, point.boundingBox!!.southwest.longitude, DELTA)
        assertEquals(2.0, point.boundingBox!!.southwest.latitude, DELTA)
        assertEquals(3.0, point.boundingBox!!.northeast.longitude, DELTA)
        assertEquals(4.0, point.boundingBox!!.northeast.latitude, DELTA)
    }

    @Test
    fun fromJson() {
        val json =
            "{ \"type\": \"Point\", \"coordinates\": [ 100, 0] }"
        val geo: Point = Point.fromJson(json)

        assertEquals(geo.longitude, 100.0, DELTA)
        assertEquals(geo.latitude, 0.0, DELTA)
        assertNull(geo.altitude)
    }

    @Test
    fun toJson() {
        val json = "{ \"type\": \"Point\", \"coordinates\": [ 100.0, 0.0] }"

        val actualPoint = Point.fromJson(Point.fromJson(json).toJson())
        val expectedPoint = Point.fromJson(json)
        assertEquals(expectedPoint, actualPoint)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            Point.fromJson("{\"type\":\"Point\",\"coordinates\":null}")
        }
    }
}
