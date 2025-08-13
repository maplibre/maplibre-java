package org.maplibre.geojson.util

import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.Point
import kotlin.test.Test
import kotlin.test.assertEquals

class LineSliceTest {

    @Test
    fun `Slice line`() {
        val line = LineString(
            points = listOf(
                Point(longitude = -97.88131713867188, latitude = 22.466878364528448),
                Point(longitude = -97.82089233398438, latitude = 22.175960091218524),
                Point(longitude = -97.6190185546875, latitude = 21.8704201873689)
            )
        )

        val startPoint = Point(longitude = -97.796173, latitude = 22.254624)
        val endPoint = Point(longitude = -97.727508, latitude = 22.057641)

        val sliced = line.slice(startPoint, endPoint)

        assertEquals(
            LineString(
                points = listOf(
                    Point(longitude = -97.83572914982011, latitude = 22.2473926902426),
                    Point(longitude = -97.82089233398438, latitude = 22.175960091218524),
                    Point(longitude = -97.73846677581922, latitude = 22.051207400679672)
                )
            ),
            sliced
        )
    }

    @Test
    fun `Slice vertical`() {
        val line = LineString(
            points = listOf(
                Point(longitude = -121.254478, latitude = 38.705824),
                Point(longitude = -121.254494, latitude = 38.709767),
            )
        )
        val startPoint = Point(longitude = -121.254478, latitude = 38.705824)
        val endPoint = Point(longitude = -121.254478, latitude = 38.706343)

        val sliced = line.slice(startPoint, endPoint)

        assertEquals(
            LineString(
                points = listOf(
                    Point(longitude = -121.254478, latitude = 38.705824),
                    Point(longitude = -121.25448010565685, latitude = 38.70634291281136)
                )
            ),
            sliced
        )
    }
}