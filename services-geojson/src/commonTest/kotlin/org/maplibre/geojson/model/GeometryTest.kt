package org.maplibre.geojson.model

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA

class GeometryTest {

    @Test
    fun fromJson() {
val json =
    """
    {
        "type": "GeometryCollection",
        "bbox": [120, 40, -120, -40],
        "geometries": [
            {
                "type": "Point",
                "bbox": [110, 30, -110, -30],
                "coordinates": [100, 0]
            },
            {
                "type": "LineString",
                "bbox": [110, 30, -110, -30],
                "coordinates": [[101, 0], [102, 1]]
            }
        ]
    }
    """.trimIndent()

        val geometry = Geometry.fromJson(json)
        assertTrue(geometry is GeometryCollection)
    }

    @Test
    fun pointFromJson() {
        val geometry = Geometry.fromJson(
            """
            {
                "coordinates": [2, 3],
                "type": "Point",
                "bbox": [1.0, 2.0, 3.0, 4.0]
            }
            """.trimIndent()
        )

        assertNotNull(geometry)
        assertNotNull(geometry.boundingBox)
        assertEquals(1.0, geometry.boundingBox!!.southwest.longitude, DELTA)
        assertEquals(2.0, geometry.boundingBox!!.southwest.latitude, DELTA)
        assertEquals(3.0, geometry.boundingBox!!.northeast.longitude, DELTA)
        assertEquals(4.0, geometry.boundingBox!!.northeast.latitude, DELTA)
        assertTrue(geometry is Point)
        assertEquals(2.0, geometry.longitude, DELTA)
        assertEquals(3.0, geometry.latitude, DELTA)
    }

    @Test
    fun pointToJson() {
        val geometry: Geometry = Point(
            2.0, 3.0, boundingBox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        )

        val actualPoint = Point.fromJson(geometry.toJson())
val expectedPoint = Point.fromJson(
    """
    {
        "coordinates": [2.0, 3.0],
        "type": "Point",
        "bbox": [1.0, 2.0, 3.0, 4.0]
    }
    """.trimIndent()
)
        assertEquals(expectedPoint, actualPoint)
    }

    @Test
    fun lineStringFromJson() {
        val lineString = Geometry.fromJson(
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
        assertNotNull(lineString.boundingBox)
        assertEquals(1.0, lineString.boundingBox!!.southwest.longitude, DELTA)
        assertEquals(2.0, lineString.boundingBox!!.southwest.latitude, DELTA)
        assertEquals(3.0, lineString.boundingBox!!.northeast.longitude, DELTA)
        assertEquals(4.0, lineString.boundingBox!!.northeast.latitude, DELTA)
        assertTrue(lineString is LineString)
        assertEquals(1.0, lineString.points[0].longitude, DELTA)
        assertEquals(2.0, lineString.points[0].latitude, DELTA)
        assertEquals(2.0, lineString.points[1].longitude, DELTA)
        assertEquals(3.0, lineString.points[1].latitude, DELTA)
        assertEquals(3.0, lineString.points[2].longitude, DELTA)
        assertEquals(4.0, lineString.points[2].latitude, DELTA)
    }

    @Test
    fun lineStringToJson() {
        val geometry: Geometry = LineString(
            listOf(
                Point(1.0, 2.0),
                Point(2.0, 3.0),
                Point(3.0, 4.0)
            ),
            BoundingBox(1.0, 2.0, 3.0, 4.0)
        )

        val actualLineString = LineString.fromJson(geometry.toJson())
        val expectedLineString = LineString.fromJson(
                    """
                    {
                        "coordinates": [
                            [1.0, 2.0],
                            [2.0, 3.0],
                            [3.0, 4.0]
                        ],
                        "type": "LineString",
                        "bbox": [1.0, 2.0, 3.0, 4.0]
                    }
                    """.trimIndent()
                )

        assertEquals(expectedLineString, actualLineString)
    }
}
