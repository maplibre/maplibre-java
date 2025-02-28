package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.assertFailsWith

class MultiPointTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)
        assertNotNull(multiPoint)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)
        assertNull(multiPoint.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val multiPoint = MultiPoint(points)

        val actualMultiPoint = MultiPoint.fromJson(multiPoint.toJson())
        val expectedMultiPoint = MultiPoint.fromJson("{\"coordinates\":[[1.0,2.0],[2.0,3.0]],\"type\":\"MultiPoint\"}")
        assertEquals(expectedMultiPoint, actualMultiPoint)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint(points, bbox)
        assertNotNull(multiPoint.boundingBox)
        assertEquals(1.0, multiPoint.boundingBox!!.west, DELTA)
        assertEquals(2.0, multiPoint.boundingBox!!.south, DELTA)
        assertEquals(3.0, multiPoint.boundingBox!!.east, DELTA)
        assertEquals(4.0, multiPoint.boundingBox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPoint = MultiPoint(points, bbox)

        val actualMultiPoint = MultiPoint.fromJson(multiPoint.toJson())
        val expectedMultiPoint = MultiPoint.fromJson("{\"coordinates\":[[1.0,2.0],[2.0,3.0]],\"type\":\"MultiPoint\",\"bbox\":[1.0,2.0,3.0,4.0]}")
        assertEquals(expectedMultiPoint, actualMultiPoint)
    }

    @Test
    fun fromJson() {
        val json = ("{ \"type\": \"MultiPoint\","
                + "\"coordinates\": [ [100, 0], [101, 1] ] } ")
        val geo: MultiPoint = MultiPoint.fromJson(json)
        assertEquals(geo.points.first().longitude, 100.0, DELTA)
        assertEquals(geo.points.first().latitude, 0.0, DELTA)
        assertEquals(geo.points[1].longitude, 101.0, DELTA)
        assertEquals(geo.points[1].latitude, 1.0, DELTA)
        assertNull(geo.points.first().altitude)
    }

    @Test
    fun toJson() {
        val json = ("{ \"type\": \"MultiPoint\","
                + "\"coordinates\": [ [100.0, 0.0], [101.0, 1.0] ] } ")
        val geo: MultiPoint = MultiPoint.fromJson(json)

        val actualMultiPoint = MultiPoint.fromJson(geo.toJson())
        val expectedMultiPoint = MultiPoint.fromJson(json)
        assertEquals(expectedMultiPoint, actualMultiPoint)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            MultiPoint.fromJson("{\"type\":\"MultiPoint\",\"coordinates\":null}")
        }
    }
}
