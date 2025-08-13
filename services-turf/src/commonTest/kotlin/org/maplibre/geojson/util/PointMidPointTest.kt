package org.maplibre.geojson.util

import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.DELTA
import kotlin.test.Test
import kotlin.test.assertEquals


class PointMidPointTest {

    @Test
    fun `Midpoint horizontal`() {
        val point1 = Point(longitude = 0.0, latitude = 0.0)
        val point2 = Point(longitude = 10.0, latitude = 0.0)

        val midpoint1 = point1.findMidpoint(point2)
        val midpoint2 = point1.findMidpoint(point2)

        assertEquals(midpoint2, midpoint1)
    }

    @Test
    fun `Midpoint vertical`() {
        val point1 = Point(longitude = 0.0, latitude = 0.0)
        val point2 = Point(longitude = 0.0, latitude = 10.0)

        val midpoint1 = point1.findMidpoint(point2)
        val midpoint2 = point1.findMidpoint(point2)

        assertEquals(midpoint2, midpoint1)
    }
}
