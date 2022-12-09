package com.mapbox.turf

import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMeasurement.Companion.distance

/**
 * Methods found in this class are meant to consume a set of information and classify it according
 * to a shared quality or characteristic.
 *
 * @since 3.0.0
 */
object TurfClassification {
    /**
     * Takes a reference point and a list of [Point] geometries and returns the point from the
     * set point list closest to the reference. This calculation is geodesic.
     *
     * @param targetPoint the reference point
     * @param points      set list of points to run against the input point
     * @return the closest point in the set to the reference point
     * @since 3.0.0
     */
    @JvmStatic
    fun nearestPoint(targetPoint: Point, points: List<Point>): Point {
        if (points.isEmpty()) {
            return targetPoint
        }
        var nearestPoint = points[0]
        var minDist = Double.POSITIVE_INFINITY
        for (point in points) {
            val distanceToPoint: Double = distance(targetPoint, point)
            if (distanceToPoint < minDist) {
                nearestPoint = point
                minDist = distanceToPoint
            }
        }
        return nearestPoint
    }
}