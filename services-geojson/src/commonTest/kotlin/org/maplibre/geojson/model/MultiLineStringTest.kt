package org.maplibre.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.assertFailsWith

class MultiLineStringTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString(lineStrings)
        assertNotNull(multiLineString)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString(lineStrings)
        assertNull(multiLineString.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString(lineStrings)

        val actualMultiLineString = MultiLineString.fromJson(multiLineString.toJson())
        val expectedMultiLineString = MultiLineString.fromJson(
            """
            {
                "type": "MultiLineString",
                "coordinates": [
                    [
                        [1.0, 2.0],
                        [2.0, 3.0]
                    ],
                    [
                        [1.0, 2.0],
                        [2.0, 3.0]
                    ]
                ]
            }
            """.trimIndent()
        )
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString(lineStrings, bbox)
        assertNotNull(multiLineString.boundingBox)
        assertEquals(1.0, multiLineString.boundingBox!!.west, DELTA)
        assertEquals(2.0, multiLineString.boundingBox!!.south, DELTA)
        assertEquals(3.0, multiLineString.boundingBox!!.east, DELTA)
        assertEquals(4.0, multiLineString.boundingBox!!.north, DELTA)
    }

    @Test
    fun passingInSingleLineString_doesHandleCorrectly() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(3.0, 4.0)
        )

        val geometry = LineString(points)
        val multiLineString = MultiLineString(listOf(geometry))

        assertNotNull(multiLineString)
        assertEquals(1, multiLineString.lineStrings.size)
        assertEquals(1.0, multiLineString.lineStrings[0].points[0].longitude, DELTA)
        assertEquals(2.0, multiLineString.lineStrings[0].points[0].latitude, DELTA)
        assertEquals(3.0, multiLineString.lineStrings[0].points[1].longitude, DELTA)
        assertEquals(4.0, multiLineString.lineStrings[0].points[1].latitude, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString(lineStrings, bbox)

        val actualMultiLineString = MultiLineString.fromJson(multiLineString.toJson())
        val expectedMultiLineString = MultiLineString.fromJson(
            """
    {
        "type": "MultiLineString",
        "bbox": [1.0, 2.0, 3.0, 4.0],
        "coordinates": [
            [
                [1.0, 2.0],
                [2.0, 3.0]
            ],
            [
                [1.0, 2.0],
                [2.0, 3.0]
            ]
        ]
    }
    """.trimIndent()
        )
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun fromJson() {
        val json = """
    {
        "type": "MultiLineString",
        "coordinates": [
            [
                [100.0, 0.0],
                [101.0, 1.0]
            ],
            [
                [102.0, 2.0],
                [103.0, 3.0]
            ]
        ]
    }
    """.trimIndent()

        val geo: MultiLineString = MultiLineString.fromJson(json)
        assertEquals(geo.lineStrings[0].points[0].longitude, 100.0, DELTA)
        assertEquals(geo.lineStrings[0].points[0].latitude, 0.0, DELTA)
        assertNull(geo.lineStrings[0].points[0].altitude)
    }

    @Test
    fun toJson() {
        val json = """
            {
                "type": "MultiLineString",
                "coordinates": [
                    [
                        [100.0, 0.0],
                        [101.0, 1.0]
                    ],
                    [
                        [102.0, 2.0],
                        [103.0, 3.0]
                    ]
                ]
            }
            """.trimIndent()
        val geo = MultiLineString.fromJson(json)

        val actualMultiLineString = MultiLineString.fromJson(geo.toJson())
        val expectedMultiLineString = MultiLineString.fromJson(json)
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            MultiLineString.fromJson(
                """
                {
                    "type": "MultiLineString",
                    "coordinates": null
                }
                """.trimIndent()
            )
        }
    }
}
