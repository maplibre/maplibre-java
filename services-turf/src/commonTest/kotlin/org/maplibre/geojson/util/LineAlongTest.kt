package org.maplibre.geojson.util

import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.FeatureCollection
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.util.TestUtils.DELTA
import org.maplibre.geojson.util.TestUtils.loadJsonFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class LineAlongTest {

    @Test
    fun `LineString along`() {
        val feature = Feature.fromJson(loadJsonFixture("turf-along/dc-line.geojson"))
        val line = feature.geometry as LineString

        val pt1 = line.along(1.0, MeasureUnit.MILES)
        val pt2 = line.along(1.2, MeasureUnit.MILES)
        val pt3 = line.along(1.4, MeasureUnit.MILES)
        val pt4 = line.along(1.6, MeasureUnit.MILES)
        val pt5 = line.along(1.8, MeasureUnit.MILES)
        val pt6 = line.along(2.0, MeasureUnit.MILES)
        val pt7 = line.along(100.0, MeasureUnit.MILES)
        val pt8 = line.along(0.0, MeasureUnit.MILES)

        val features = listOf(
            Feature(pt1),
            Feature(pt2),
            Feature(pt3),
            Feature(pt4),
            Feature(pt5),
            Feature(pt6),
            Feature(pt7),
            Feature(pt8)
        )
        val fc = FeatureCollection(features)

        assertEquals(8, fc.features.size)
        assertEquals((fc.features[7].geometry as Point).longitude, pt8.longitude, DELTA)
        assertEquals((fc.features[7].geometry as Point).latitude, pt8.latitude, DELTA)
    }
}