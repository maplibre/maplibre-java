package org.maplibre.geojson.util

import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.DELTA
import kotlin.test.Test
import kotlin.test.assertEquals


class PointBearingTest {

    @Test
    fun `Bearing to point`() {
        val point1 = Point(longitude = -75.0, latitude = 45.0)
        val point2 = Point(longitude = 20.0, latitude = 60.0)

        val bearing = point1.bearingTo(point2)

        assertEquals(37.75, bearing, DELTA)
    }
}