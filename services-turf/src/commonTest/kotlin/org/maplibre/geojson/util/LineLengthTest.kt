package org.maplibre.geojson.util

import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.util.TestUtils.loadJsonFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class LineLengthTest {

    @Test
    fun `LineString length route1`() {
        val line = Feature.fromJson(loadJsonFixture("turf-line-distance/route1.geojson"))
            .geometry as LineString
        val length = line.length(MeasureUnit.MILES)
        assertEquals(202.0, length, 1.0)
    }

    @Test
    fun `LineString length route2`() {
        val line = Feature.fromJson(loadJsonFixture("turf-line-distance/route2.geojson"))
            .geometry as LineString
        val length = line.length(MeasureUnit.KILOMETERS)
        assertEquals(741.778739, length, 1e-6)
    }
}