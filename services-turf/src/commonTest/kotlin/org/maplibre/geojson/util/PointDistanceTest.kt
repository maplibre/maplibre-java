package org.maplibre.geojson.util

import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.DELTA
import kotlin.test.Test
import kotlin.test.assertEquals


class PointDistanceTest {

    @Test
    fun `Distance to point miles`() {
        val point1 = Point(longitude = -75.343, latitude = 39.984)
        val point2 = Point(longitude = -75.534, latitude = 39.123)

        val bearing = point1.distanceTo(point2, MeasureUnit.MILES)

        assertEquals(60.0, bearing, 1.0)
    }

    @Test
    fun `Distance to point kilometers`() {
        val point1 = Point(longitude = -75.343, latitude = 39.984)
        val point2 = Point(longitude = -75.534, latitude = 39.123)

        val bearing = point1.distanceTo(point2, MeasureUnit.KILOMETERS)

        assertEquals(97.0, bearing, 1.0)
    }
}
