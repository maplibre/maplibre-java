package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.assertFailsWith

class MultiPolygonTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            Polygon(outer),
            Polygon(outer)
        )
        val multiPolygon = MultiPolygon(polygons)
        assertNotNull(multiPolygon)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            Polygon(outer),
            Polygon(outer)
        )
        val multiPolygon = MultiPolygon(polygons)
        assertNull(multiPolygon.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            Polygon(outer),
            Polygon(outer)
        )
        val multiPolygon = MultiPolygon(polygons)

        val actualMultiPolygon = MultiPolygon.fromJson(multiPolygon.toJson())
        val expectedMultiPolygon = MultiPolygon.fromJson(
            """
            {
                "type": "MultiPolygon",
                "coordinates": [
                    [
                        [
                            [1.0, 2.0],
                            [2.0, 3.0],
                            [3.0, 4.0],
                            [1.0, 2.0]
                        ]
                    ],
                    [
                        [
                            [1.0, 2.0],
                            [2.0, 3.0],
                            [3.0, 4.0],
                            [1.0, 2.0]
                        ]
                    ]
                ]
            }
            """.trimIndent()
        )
        assertEquals(expectedMultiPolygon, actualMultiPolygon)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            Polygon(outer),
            Polygon(outer)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPolygon = MultiPolygon(polygons, bbox)
        assertNotNull(multiPolygon.boundingBox)
        assertEquals(1.0, multiPolygon.boundingBox!!.west, DELTA)
        assertEquals(2.0, multiPolygon.boundingBox!!.south, DELTA)
        assertEquals(3.0, multiPolygon.boundingBox!!.east, DELTA)
        assertEquals(4.0, multiPolygon.boundingBox!!.north, DELTA)
    }

    @Test
    fun passingInSinglePolygon_doesHandleCorrectly() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(3.0, 4.0),
            Point(5.0, 6.0),
            Point(1.0, 2.0),
        )

        val polygon = Polygon(LineString(points))
        val multiPolygon = MultiPolygon(listOf(polygon))
        assertNotNull(multiPolygon)
        assertEquals(1, multiPolygon.polygons.size)
        assertEquals(
            2.0,
            multiPolygon.polygons.first().outerLineStringRing.points.first().latitude,
            DELTA
        )
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0),
            Point(3.0, 4.0),
            Point(1.0, 2.0),
        )

        val outer = LineString(points)
        val polygons = listOf(
            Polygon(outer),
            Polygon(outer)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val multiPolygon = MultiPolygon(polygons, bbox)

        val actualMultiPolygon = MultiPolygon.fromJson(multiPolygon.toJson())
        val expectedMultiPolygon = MultiPolygon.fromJson(
            """
            {
                "type": "MultiPolygon",
                "bbox": [1.0, 2.0, 3.0, 4.0],
                "coordinates": [
                    [
                        [
                            [1.0, 2.0],
                            [2.0, 3.0],
                            [3.0, 4.0],
                            [1.0, 2.0]
                        ]
                    ],
                    [
                        [
                            [1.0, 2.0],
                            [2.0, 3.0],
                            [3.0, 4.0],
                            [1.0, 2.0]
                        ]
                    ]
                ]
            }
            """.trimIndent()
        )
        assertEquals(expectedMultiPolygon, actualMultiPolygon)
    }

    @Test
    fun fromJson() {
        val json = """
                                        {
                                            "type": "MultiPolygon",
                                            "coordinates": [
                                                [
                                                    [
                                                        [102, 2],
                                                        [103, 2],
                                                        [103, 3],
                                                        [102, 3],
                                                        [102, 2]
                                                    ]
                                                ],
                                                [
                                                    [
                                                        [100, 0],
                                                        [101, 0],
                                                        [101, 1],
                                                        [100, 1],
                                                        [100, 0]
                                                    ],
                                                    [
                                                        [100.2, 0.2],
                                                        [100.2, 0.8],
                                                        [100.8, 0.8],
                                                        [100.8, 0.2],
                                                        [100.2, 0.2]
                                                    ]
                                                ]
                                            ]
                                        }
                                        """.trimIndent()
        val geo = MultiPolygon.fromJson(json)
        assertEquals(geo.polygons.first().outerLineStringRing.points.first().longitude, 102.0, DELTA)
        assertEquals(geo.polygons.first().outerLineStringRing.points.first().latitude, 2.0, DELTA)
        assertNull(geo.polygons.first().outerLineStringRing.points.first().altitude)
    }

    @Test
    fun toJson() {
        val json = """
                    {
                        "type": "MultiPolygon",
                        "coordinates": [
                            [
                                [
                                    [102.0, 2.0],
                                    [103.0, 2.0],
                                    [103.0, 3.0],
                                    [102.0, 3.0],
                                    [102.0, 2.0]
                                ]
                            ],
                            [
                                [
                                    [100.0, 0.0],
                                    [101.0, 0.0],
                                    [101.0, 1.0],
                                    [100.0, 1.0],
                                    [100.0, 0.0]
                                ],
                                [
                                    [100.2, 0.2],
                                    [100.2, 0.8],
                                    [100.8, 0.8],
                                    [100.8, 0.2],
                                    [100.2, 0.2]
                                ]
                            ]
                        ]
                    }
                    """.trimIndent()

        val multiPolygon = MultiPolygon.fromJson(json)

        val actualMultiPolygon = MultiPolygon.fromJson(multiPolygon.toJson())
        val expectedMultiPolygon = MultiPolygon.fromJson(json)
        assertEquals(expectedMultiPolygon, actualMultiPolygon)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            MultiPolygon.fromJson(
                """
                {
                    "type": "MultiPolygon",
                    "coordinates": null
                }
                """.trimIndent()
            )
        }
    }
}
