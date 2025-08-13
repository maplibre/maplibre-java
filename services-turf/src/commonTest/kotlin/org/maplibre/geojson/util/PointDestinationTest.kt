package org.maplibre.geojson.util

import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.DELTA
import kotlin.test.Test
import kotlin.test.assertEquals


class PointDestinationTest {

    @Test
    fun `Destination bearing 0`() {
        val point = Point(longitude = -75.0, latitude = 38.10096062273525)

        val destination = point.destination(0.0, 100.0, MeasureUnit.MILES)

        assertEquals(Point(longitude = -75.00000000000001, latitude = 38.10096062273525), destination)
    }
}
