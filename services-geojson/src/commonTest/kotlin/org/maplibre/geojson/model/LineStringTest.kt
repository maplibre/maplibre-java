package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.assertFailsWith

class LineStringTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0),
        )

        val lineString = LineString(points)
        assertNotNull(lineString)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)
        assertNull(lineString.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)

        val actualLineString = LineString.fromJson(lineString.toJson())
        val expectedLineString = LineString.fromJson(
            """
            {
                "type": "LineString",
                "coordinates": [
                    [1.0, 1.0],
                    [2.0, 2.0],
                    [3.0, 3.0]
                ]
            }
            """.trimIndent()
        )
        assertEquals(expectedLineString, actualLineString)
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
        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val lineString = LineString(points, bbox)

        val actualLineString = LineString.fromJson(lineString.toJson())
        val expectedLineString = LineString.fromJson(
            """
            {
                "type": "LineString",
                "bbox": [1.0, 2.0, 3.0, 4.0],
                "coordinates": [
                    [1.0, 1.0],
                    [2.0, 2.0],
                    [3.0, 3.0]
                ]
            }
            """.trimIndent()
        )

        assertEquals(expectedLineString, actualLineString)
    }

    @Test
    fun bbox_doesDeserializeWhenPresent() {
        val lineString = LineString.fromJson(
            """
            {
                "coordinates": [
                    [1, 2],
                    [2, 3],
                    [3, 4]
                ],
                "type": "LineString",
                "bbox": [1.0, 2.0, 3.0, 4.0]
            }
            """.trimIndent()
        )

        assertNotNull(lineString)
        assertEquals(1.0, lineString.points[0].longitude, DELTA)
        assertEquals(2.0, lineString.points[0].latitude, DELTA)
        assertEquals(2.0, lineString.points[1].longitude, DELTA)
        assertEquals(3.0, lineString.points[1].latitude, DELTA)
        assertEquals(3.0, lineString.points[2].longitude, DELTA)
        assertEquals(4.0, lineString.points[2].latitude, DELTA)

        assertNotNull(lineString.boundingBox)
        assertEquals(1.0, lineString.boundingBox!!.southwest.longitude, DELTA)
        assertEquals(2.0, lineString.boundingBox!!.southwest.latitude, DELTA)
        assertEquals(3.0, lineString.boundingBox!!.northeast.longitude, DELTA)
        assertEquals(4.0, lineString.boundingBox!!.northeast.latitude, DELTA)
    }

    @Test
    fun fromJson() {
        val json =
            """
            {
                "type": "LineString",
                "coordinates": [
                    [100, 0],
                    [101, 1]
                ]
            }
            """.trimIndent()
        val geo: LineString = LineString.fromJson(json)
        assertEquals(geo.points.first().longitude, 100.0, 0.0)
        assertEquals(geo.points.first().latitude, 0.0, 0.0)
        assertNull(geo.points.first().altitude)
    }

    @Test
    fun toJson() {
        val json =
            """
            {
                "type": "LineString",
                "coordinates": [
                    [100.0, 0.0],
                    [101.0, 1.0]
                ]
            }
            """.trimIndent()
        val geo: LineString = LineString.fromJson(json)
        val geoJsonString = geo.toJson()

        val actualLineString = LineString.fromJson(geoJsonString)
        val expectedLineString = LineString.fromJson(json)
        assertEquals(expectedLineString, actualLineString)
    }

    @Test
    fun fromJson_coordinatesNotPresent() {
        assertFailsWith(SerializationException::class) {
            LineString.fromJson(
                """
                {
                    "type": "LineString",
                    "coordinates": null
                }
                """.trimIndent()
            )
        }
    }
}
