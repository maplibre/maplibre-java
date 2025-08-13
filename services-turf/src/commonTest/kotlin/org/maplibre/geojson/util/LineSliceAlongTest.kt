package org.maplibre.geojson.util

import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.loadJsonFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class LineSliceAlongTest {

    @Test
    fun testTurfLineSliceLine1() {
        val line = Feature.fromJson(loadJsonFixture("turf-line-slice-along/line1.geojson"))
            .geometry as LineString

        val sliced = line.lineSliceAlong( 500.0, 750.0, MeasureUnit.MILES)

        assertEquals(Point(longitude=119.79418848497724, latitude=26.783667457250907), sliced.points.first())
        assertEquals(Point(longitude=121.59667968749999, latitude=29.96215888639612), sliced.points.last())
    }

    @Test
    fun testTurfLineSliceRoute2() {
        val line = Feature.fromJson(loadJsonFixture("turf-line-slice-along/route2.geojson"))
            .geometry as LineString

        val sliced = line.lineSliceAlong(25.0, 50.0, MeasureUnit.MILES)

        assertNotNull(sliced)
        assertEquals(Point(longitude=13.064497273502463, latitude=41.74686370229959), sliced.points.first())
        assertEquals(Point(longitude=13.458155735570484, latitude=41.54986535346629), sliced.points.last())
    }
}