package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.assertFailsWith

class PolygonTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val polygon = Polygon(LineString(points))
        assertNotNull(polygon)
    }

    @Test
    fun fromOuterInner_throwsNotLinearRingException() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0)
        )

        assertFailsWith(IllegalArgumentException::class) {
            Polygon(LineString(points))
        }
    }

    @Test
    fun fromOuterInner_throwsNotConnectedLinearRingException() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(5.0, 2.0),
        )

        assertFailsWith(IllegalArgumentException::class) {
            Polygon(LineString(points))
        }
    }

    @Test
    fun fromOuterInner_handlesSingleLineStringCorrectly() {
        val points = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )

        val polygon = Polygon(LineString(points))
        assertEquals(Point(10.0, 2.0), polygon.outerLineStringRing.points.first())
    }

    @Test
    fun fromOuterInner_handlesOuterAndInnerLineStringCorrectly() {
        val outerPoints = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )
        val outerLineString = LineString(outerPoints)

        val innerPoints = listOf(
            Point(5.0, 2.0),
            Point(2.5, 2.0),
            Point(1.5, 2.0),
            Point(5.0, 2.0),
        )
        val innerLineString = LineString(innerPoints)

        val polygon = Polygon(outerLineString, listOf(innerLineString))
        assertEquals(outerLineString, polygon.outerLineStringRing)
        assertEquals(1, polygon.holeLineStringRings.size)
        assertEquals(innerLineString, polygon.holeLineStringRings.first())
    }

    @Test
    fun fromOuterInner_withABoundingBox() {
        val outerPoints = listOf(
            Point(10.0, 2.0),
            Point(5.0, 2.0),
            Point(3.0, 2.0),
            Point(10.0, 2.0),
        )
        val outerLineString = LineString(outerPoints)

        val innerPoints = listOf(
            Point(5.0, 2.0),
            Point(2.5, 2.0),
            Point(1.5, 2.0),
            Point(5.0, 2.0),
        )
        val innerLineString = LineString(innerPoints)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon(outerLineString, listOf(innerLineString), bbox)

        assertEquals(bbox, polygon.boundingBox)
        assertEquals(outerLineString, polygon.outerLineStringRing)
        assertEquals(1, polygon.holeLineStringRings.size)
        assertEquals(innerLineString, polygon.holeLineStringRings.first())
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))

        val polygon = Polygon(outerLine, inner = innerLines)
        assertNull(polygon.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outerLine = LineString(points)

        val innerLines = listOf(LineString(points), LineString(points))
        val polygon = Polygon(outerLine, innerLines)

        val actualPolygon = Polygon.fromJson(polygon.toJson())
        val expectedPolygon = Polygon.fromJson(
            """
                    {
                        "type": "Polygon",
                        "coordinates": [
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ],
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ],
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ]
                        ]
                    }
                    """
        )
        assertEquals(expectedPolygon, actualPolygon)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon(outerLine, innerLines, bbox)

        assertNotNull(polygon.boundingBox)
        assertEquals(1.0, polygon.boundingBox!!.west, DELTA)
        assertEquals(2.0, polygon.boundingBox!!.south, DELTA)
        assertEquals(3.0, polygon.boundingBox!!.east, DELTA)
        assertEquals(4.0, polygon.boundingBox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0)
        )

        val outerLine = LineString(points)
        val innerLines = listOf(LineString(points), LineString(points))
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val polygon = Polygon(outerLine, innerLines, bbox)

        val actualPolygon = Polygon.fromJson(polygon.toJson())
        val expectedPolygon = Polygon.fromJson(
            """
                    {
                        "type": "Polygon",
                        "bbox": [1.0, 2.0, 3.0, 4.0],
                        "coordinates": [
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ],
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ],
                            [
                                [1.0, 2.0],
                                [2.0, 3.0],
                                [3.0, 4.0],
                                [1.0, 2.0]
                            ]
                        ]
                    }
                    """
        )
        assertEquals(expectedPolygon, actualPolygon)
    }

    @Test
    fun fromJson() {
        val json = "{\"type\": \"Polygon\", " +
                "\"coordinates\": [[[100, 0], [101, 0], [101, 1], [100, 1],[100, 0]]]}"
        val geo = Polygon.fromJson(json)
        assertEquals(100.0, geo.outerLineStringRing.points.first().longitude, DELTA)
        assertEquals(0.0, geo.outerLineStringRing.points.first().latitude, DELTA)
        assertNull(geo.outerLineStringRing.points.first().altitude)
    }

    @Test
    fun fromJsonHoles() {
        val json = """
                    {
                        "type": "Polygon",
                        "coordinates": [
                            [
                                [100, 0],
                                [101, 0],
                                [101, 1],
                                [100, 1],
                                [100, 0]
                            ],
                            [
                                [100.8, 0.8],
                                [100.8, 0.2],
                                [100.2, 0.2],
                                [100.2, 0.8],
                                [100.8, 0.8]
                            ]
                        ]
                    }
                    """.trimIndent()
        val geo: Polygon = Polygon.fromJson(json)
        assertEquals(100.0, geo.outerLineStringRing.points.first().longitude, DELTA)
        assertEquals(0.0, geo.outerLineStringRing.points.first().latitude, DELTA)
        assertEquals(1, geo.holeLineStringRings.size)
        assertEquals(100.8, geo.holeLineStringRings.first().points.first().longitude, DELTA)
        assertEquals(0.8, geo.holeLineStringRings.first().points.first().latitude, DELTA)
        assertNull(geo.holeLineStringRings.first().points.first().altitude)
    }

    @Test
    fun toJson() {
        val json = """
            {
                "type": "Polygon",
                "coordinates": [
                    [
                        [100.0, 0.0],
                        [101.0, 0.0],
                        [101.0, 1.0],
                        [100.0, 1.0],
                        [100.0, 0.0]
                    ]
                ]
            }
            """.trimIndent()

        val actualPolygon = Polygon.fromJson(Polygon.fromJson(json).toJson())
        val expectedPolygon = Polygon.fromJson(json)
        assertEquals(expectedPolygon, actualPolygon)
    }

    @Test
    fun toJsonHoles() {
        val json = """
                {
                    "type": "Polygon",
                    "coordinates": [
                        [
                            [100.0, 0.0],
                            [101.0, 0.0],
                            [101.0, 1.0],
                            [100.0, 1.0],
                            [100.0, 0.0]
                        ],
                        [
                            [100.8, 0.8],
                            [100.8, 0.2],
                            [100.2, 0.2],
                            [100.2, 0.8],
                            [100.8, 0.8]
                        ]
                    ]
                }
                """.trimIndent()

        val actualPolygon = Polygon.fromJson(Polygon.fromJson(json).toJson())
        val expectedPolygon = Polygon.fromJson(json)
        assertEquals(expectedPolygon, actualPolygon)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            Polygon.fromJson("{\"type\":\"Polygon\",\"coordinates\":null}")
        }
    }
}
