package org.maplibre.geojson.util

import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon

/**
 * Generates a [Polygon] approximating a circle around this [Point].
 *
 * @param radius The radius of the circle.
 * @param steps The number of points to use for the circle approximation (default is 64).
 * @param unit The unit of measurement for the radius (default is [MeasureUnit.DEFAULT]).
 * @return A [Polygon] representing the circle.
 * @throws IllegalArgumentException if steps is less than 1.
 */
fun Point.circle(
    radius: Double,
    steps: Int = 64,
    unit: MeasureUnit = MeasureUnit.DEFAULT
): Polygon {
    require(steps >= 1) { "Steps must be greater than 0" }

    val coordinates = (0 until steps)
        .map { value ->
            this.destination(radius, value * 360.0 / steps, unit)
        }

    return Polygon(outerLineStringRing = LineString(points = coordinates + coordinates.first()))
}
