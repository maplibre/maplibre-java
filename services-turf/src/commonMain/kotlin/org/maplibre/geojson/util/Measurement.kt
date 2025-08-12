package org.maplibre.geojson.util

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.maplibre.geojson.model.Feature
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Point
import org.maplibre.geojson.model.Polygon
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the length of a [LineString] in the specified units.
 *
 * @param unit The unit of measurement to use.
 * @return The length of the line in the specified units.
 */
fun LineString.length(unit: MeasureUnit): Double = points.length(unit)

/**
 * Calculates the total length of a [MultiLineString] in the specified units.
 *
 * @param unit The unit of measurement to use.
 * @return The combined length of all line strings in the specified units.
 */
fun MultiLineString.length(unit: MeasureUnit): Double =
    lineStrings.sumOf { lineString -> lineString.length(unit) }

/**
 * Calculates the perimeter of a [Polygon] in the specified units, including holes.
 *
 * @param unit The unit of measurement to use.
 * @return The total perimeter of the polygon in the specified units.
 */
fun Polygon.length(unit: MeasureUnit): Double =
    outerLineStringRing.length(unit) +
            holeLineStringRings.sumOf { line -> line.length(unit) }

/**
 * Calculates the total perimeter of a [MultiPolygon] in the specified units, including holes.
 *
 * @param unit The unit of measurement to use.
 * @return The total perimeter of all polygons in the specified units.
 */
fun MultiPolygon.length(unit: MeasureUnit): Double =
    polygons.sumOf { polygon -> polygon.length(unit) }

/**
 * Calculates the length of a list of [Point]s as a line in the specified units.
 *
 * @param unit The unit of measurement to use.
 * @return The length of the line formed by the points in the specified units.
 */
private fun List<Point>.length(unit: MeasureUnit): Double {
    return drop(1)
        .mapIndexed { index, point ->
            // Using unmodified index for previous point is working,
            // because we drop the first point
            get(index).distanceTo(point, unit)
        }
        .sum()
}

/**
 * Finds the midpoint between this [Point] and another [Point].
 *
 * @param to The other point.
 * @return The midpoint as a [Point].
 */
fun Point.findMidpoint(to: Point): Point {
    val dist = this.distanceTo(to, MeasureUnit.MILES)
    val heading = this.bearingTo(to)
    return this.destination(dist / 2, heading, MeasureUnit.MILES)
}

/**
 * Returns a [Point] at a specified distance along a [LineString].
 *
 * @param distance The distance along the line.
 * @param unit The unit of measurement.
 * @return The point at the specified distance.
 */
fun LineString.along(distance: Double, unit: MeasureUnit): Point {
    return points.along(distance, unit)
}

/**
 * Returns a [Point] at a specified distance along a list of [Point]s.
 *
 * @param distance The distance along the line.
 * @param unit The unit of measurement.
 * @return The point at the specified distance.
 */
private fun List<Point>.along(distance: Double, unit: MeasureUnit): Point {
    var travelled = 0.0
    for ((index, point) in withIndex()) {
        if (travelled >= distance) {
            val overshot = distance - travelled
            if (overshot == 0.0 || index == 0) {
                return point
            } else {
                val direction = this[index].bearingTo(this[index - 1]) - 180
                return point.destination(overshot, direction, unit)
            }
        } else if (index < this.size - 1) {
            travelled += this[index].distanceTo(this[index + 1], unit)
        }
    }

    return this.last()
}

/**
 * Calculates the bearing from this [Point] to another [Point].
 *
 * @param toPoint The destination point.
 * @return The bearing in degrees.
 */
fun Point.bearingTo(toPoint: Point): Double {
    val lon1 = degreesToRadians(longitude)
    val lon2 = degreesToRadians(toPoint.longitude)
    val lat1 = degreesToRadians(latitude)
    val lat2 = degreesToRadians(toPoint.latitude)
    val value1 = sin(lon2 - lon1) * cos(lat2)
    val value2 = cos(lat1) * sin(lat2) - (sin(lat1) * cos(lat2) * cos(lon2 - lon1))

    return radiansToDegrees(atan2(value1, value2))
}

/**
 * Converts degrees to radians.
 *
 * @param degrees The value in degrees.
 * @return The value in radians.
 */
fun degreesToRadians(degrees: Double): Double {
    val radians = degrees % 360
    return radians * PI / 180
}

/**
 * Converts radians to degrees.
 *
 * @param radians The value in radians.
 * @return The value in degrees.
 */
fun radiansToDegrees(radians: Double): Double {
    val degrees = radians % (2 * PI)
    return degrees * 180 / PI
}

/**
 * Converts radians to a length in the specified unit.
 *
 * @param radians The value in radians.
 * @param unit The unit of measurement.
 * @return The length in the specified unit.
 */
fun radiansToLength(radians: Double, unit: MeasureUnit = MeasureUnit.DEFAULT): Double {
    return radians * unit.factor
}

/**
 * Converts a length in the specified unit to radians.
 *
 * @param distance The distance to convert.
 * @param unit The unit of measurement.
 * @return The value in radians.
 */
fun lengthToRadians(distance: Double, unit: MeasureUnit = MeasureUnit.DEFAULT): Double {
    return distance / unit.factor
}

/**
 * Converts a length from one unit to another.
 *
 * @param distance The distance to convert.
 * @param originalUnit The original unit of measurement.
 * @param finalUnit The target unit of measurement.
 * @return The converted length.
 */
fun convertLength(distance: Double, originalUnit: MeasureUnit, finalUnit: MeasureUnit = MeasureUnit.DEFAULT): Double {
    return radiansToLength(lengthToRadians(distance, originalUnit), finalUnit)
}

/**
 * Calculates the distance from this [Point] to another [Point] in the specified unit.
 *
 * @param point The destination point.
 * @param unit The unit of measurement.
 * @return The distance between the points.
 */
fun Point.distanceTo(
    point: Point,
    unit: MeasureUnit = MeasureUnit.DEFAULT
): Double {
    val difLat = degreesToRadians((point.latitude - this.latitude))
    val difLon = degreesToRadians((point.longitude - this.longitude))
    val lat1 = degreesToRadians(this.latitude)
    val lat2 = degreesToRadians(point.latitude)

    val value = sin(difLat / 2).pow(2.0) + sin(difLon / 2).pow(2.0) * cos(lat1) * cos(lat2)

    return radiansToLength(
        2 * atan2(sqrt(value), sqrt(1 - value)), unit
    )
}

/**
 * Returns a [LineString] that is a slice between two [Point]s along the line.
 *
 * @param startPoint The starting point.
 * @param stopPoint The ending point.
 * @return The sliced [LineString].
 * @throws IllegalArgumentException if start and stop points are equal.
 */
fun LineString.slice(startPoint: Point, stopPoint: Point): LineString {
    require(startPoint != stopPoint) { "Start and stop points in Turf lineSlice cannot equal each other." }

    val startVertex = closestPointOnLineInternal(startPoint, points)
    val stopVertex = closestPointOnLineInternal(stopPoint, points)
    val ends = mutableListOf<DistancePoint>()
    if (startVertex.index <= stopVertex.index) {
        ends.add(startVertex)
        ends.add(stopVertex)
    } else {
        ends.add(stopVertex)
        ends.add(startVertex)
    }

    val pts = mutableListOf<Point>()
    pts.add(ends[0].point)
    for (i in ends[0].index + 1 until ends[1].index + 1) {
        pts.add(points[i])
    }
    pts.add(ends[1].point)

    return LineString(pts)
}

/**
 * Returns a [LineString] that is a slice between two distances along the line.
 *
 * @param startDistance The starting distance.
 * @param stopDistance The ending distance.
 * @param unit The unit of measurement.
 * @return The sliced [LineString].
 * @throws IllegalArgumentException if startDistance or stopDistance are invalid.
 */
fun LineString.lineSliceAlong(
    startDistance: Double,
    stopDistance: Double,
    unit: MeasureUnit
): LineString {
    require(startDistance >= 0) { "startDistance must be greater than or equal 0" }
    require(stopDistance > 0) { "stopDist must be greater than 0" }
    require(startDistance != stopDistance) { "Start and stop distance in Turf lineSliceAlong cannot equal each other." }

    var travelled = 0.0
    val slicedLinePoints = mutableListOf<Point>()
    for ((index, point) in points.withIndex()) {
        if (travelled >= startDistance) {
            // Travelled distance is greater than `startDist`

            if (slicedLinePoints.isEmpty()) {
                // First point after `startDist`
                val overshot = startDistance - travelled
                if (overshot == 0.0 || index == 0) {
                    slicedLinePoints.add(point)
                } else {
                    val direction = point.bearingTo(points[index - 1]) - 180
                    val interpolated = point.destination(overshot, direction, unit)
                    slicedLinePoints.add(interpolated)
                }
            }

            if (travelled >= stopDistance) {
                // `stopDist` has been reached
                val overshot = stopDistance - travelled
                if (overshot == 0.0 || index == 0) {
                    slicedLinePoints.add(point)
                } else {
                    val direction = point.bearingTo(points[index - 1]) - 180
                    val interpolated = point.destination(overshot, direction, unit)
                    slicedLinePoints.add(interpolated)
                }

                break // Line slice finished
            } else {
                // Point between `startDist` and `stopDist`
                slicedLinePoints.add(point)
            }
        }

        points.getOrNull(index + 1)?.let { upcomingPoint ->
            travelled += point.distanceTo(upcomingPoint, unit)
        }
    }

    require(travelled >= startDistance) { "Start position is beyond line" }
    return LineString(slicedLinePoints)
}

/**
 * Finds the closest point on a [LineString] to a given [Point].
 *
 * @param point The target point.
 * @param unit The unit of measurement.
 * @return A [Feature] containing the closest point and properties for index and distance.
 */
fun LineString.closestPoint(point: Point, unit: MeasureUnit = MeasureUnit.DEFAULT): Feature {
    val distancePoint = closestPointOnLineInternal(point, points, unit)
    return Feature(
        geometry = distancePoint.point,
        properties = JsonObject(
            mapOf(
                INDEX_KEY to JsonPrimitive(distancePoint.index),
                DISTANCE_KEY to JsonPrimitive(distancePoint.distance),
            )
        )
    )
}

/**
 * Finds the nearest [Point] in a list to the target [Point].
 *
 * @param points The list of points.
 * @return The nearest point in the list.
 */
fun Point.closestPoint(points: List<Point>): Point {
    return points.minByOrNull { point -> this.distanceTo(point) }
        ?: this
}

/**
 * Calculates the destination [Point] given a distance and bearing from this [Point].
 *
 * @param distance The distance to travel.
 * @param bearing The bearing in degrees.
 * @param unit The unit of measurement.
 * @return The destination [Point].
 */
fun Point.destination(distance: Double, bearing: Double, unit: MeasureUnit): Point {
    val longitude1 = degreesToRadians(longitude)
    val latitude1 = degreesToRadians(latitude)
    val bearingRad = degreesToRadians(bearing)

    val radians = lengthToRadians(distance, unit)

    val latitude2 = asin(
        sin(latitude1) * cos(radians) + cos(latitude1) * sin(radians) * cos(bearingRad)
    )
    val longitude2 = longitude1 + atan2(
        sin(bearingRad) * sin(radians) * cos(latitude1),
        cos(radians) - sin(latitude1) * sin(latitude2)
    )

    return Point(
        radiansToDegrees(longitude2),
        radiansToDegrees(latitude2)
    )
}

private const val INDEX_KEY = "index"
private const val DISTANCE_KEY = "dist"

/**
 * Finds the closest point on a line to a given [Point], returning a [DistancePoint].
 *
 * @param point The target point.
 * @param coordinates The list of points forming the line.
 * @param units The unit of measurement.
 * @return The closest [DistancePoint] on the line.
 */
private fun closestPointOnLineInternal(
    point: Point,
    coordinates: List<Point>,
    units: MeasureUnit = MeasureUnit.KILOMETERS
): DistancePoint {
    var closestPt = DistancePoint(
        point = Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
        distance = Double.POSITIVE_INFINITY,
        index = 0,
    )

    for (i in 0 until coordinates.size - 1) {
        val start = DistancePoint(
            point = coordinates[i],
            point.distanceTo(coordinates[i], units),
            index = -1,
        )
        val stop = DistancePoint(
            point = coordinates[i + 1],
            point.distanceTo(coordinates[i + 1], units),
            index = -1,
        )

        //perpendicular
        val heightDistance: Double = max(
            start.distance,
            stop.distance
        )
        val direction = start.point.bearingTo(stop.point)
        val perpendicularPt1 = point.destination(heightDistance, direction + 90, units)
        val perpendicularPt2 = point.destination(heightDistance, direction - 90, units)

        val intersect = lineIntersects(
            perpendicularPt1.longitude,
            perpendicularPt1.latitude,
            perpendicularPt2.longitude,
            perpendicularPt2.latitude,
            start.point.longitude,
            start.point.latitude,
            stop.point.longitude,
            stop.point.latitude
        )

        var intersectDistancePoint: DistancePoint? = null
        if (intersect != null) {
            val intersectPoint =
                Point(
                    longitude = intersect.horizontalIntersection!!,
                    latitude = intersect.verticalIntersection!!
                )
            intersectDistancePoint = DistancePoint(
                point = intersectPoint,
                distance = point.distanceTo(intersectPoint, units),
                index = -1,
            )
        }

        if (start.distance < closestPt.distance) {
            closestPt = start.copy(index = i)
        }

        if (stop.distance < closestPt.distance) {
            closestPt = stop.copy(index = i)
        }

        if (intersectDistancePoint != null && (intersectDistancePoint.distance < closestPt.distance)) {
            closestPt = intersectDistancePoint.copy(index = i)
        }
    }

    return closestPt
}

/**
 * Determines if two line segments intersect and returns intersection details.
 *
 * @param line1StartX X coordinate of line 1 start.
 * @param line1StartY Y coordinate of line 1 start.
 * @param line1EndX X coordinate of line 1 end.
 * @param line1EndY Y coordinate of line 1 end.
 * @param line2StartX X coordinate of line 2 start.
 * @param line2StartY Y coordinate of line 2 start.
 * @param line2EndX X coordinate of line 2 end.
 * @param line2EndY Y coordinate of line 2 end.
 * @return [LineIntersects] if intersection exists, null otherwise.
 */
private fun lineIntersects(
    line1StartX: Double,
    line1StartY: Double,
    line1EndX: Double,
    line1EndY: Double,
    line2StartX: Double,
    line2StartY: Double,
    line2EndX: Double,
    line2EndY: Double
): LineIntersects? {
    // If the lines intersect, the result contains the x and y of the intersection
    // (treating the lines as infinite) and booleans for whether line segment 1 or line
    // segment 2 contain the point
    var result = LineIntersects(
        onLine1 = false,
        onLine2 = false
    )

    val denominator = (((line2EndY - line2StartY) * (line1EndX - line1StartX))
            - ((line2EndX - line2StartX) * (line1EndY - line1StartY)))
    if (denominator == 0.0) {
        return if (result.horizontalIntersection != null && result.verticalIntersection != null) {
            result
        } else {
            null
        }
    }
    var varA = line1StartY - line2StartY
    var varB = line1StartX - line2StartX
    val numerator1 = ((line2EndX - line2StartX) * varA) - ((line2EndY - line2StartY) * varB)
    val numerator2 = ((line1EndX - line1StartX) * varA) - ((line1EndY - line1StartY) * varB)
    varA = numerator1 / denominator
    varB = numerator2 / denominator

    // if we cast these lines infinitely in both directions, they intersect here:
    result = result.copy(
        horizontalIntersection = line1StartX + (varA * (line1EndX - line1StartX)),
        verticalIntersection = line1StartY + (varA * (line1EndY - line1StartY))
    )

    // if line1 is a segment and line2 is infinite, they intersect if:
    if (varA > 0 && varA < 1) {
        result = result.copy(onLine1 = true)
    }
    // if line2 is a segment and line1 is infinite, they intersect if:
    if (varB > 0 && varB < 1) {
        result = result.copy(onLine2 = true)
    }
    // if line1 and line2 are segments, they intersect if both of the above are true
    return if (result.onLine1 && result.onLine2) {
        result
    } else {
        null
    }
}

/**
 * Data class representing the result of a line intersection.
 *
 * @property horizontalIntersection The X coordinate of the intersection.
 * @property verticalIntersection The Y coordinate of the intersection.
 * @property onLine1 True if the intersection is on the first line segment.
 * @property onLine2 True if the intersection is on the second line segment.
 */
data class LineIntersects(
    val horizontalIntersection: Double? = null,
    val verticalIntersection: Double? = null,
    val onLine1: Boolean,
    val onLine2: Boolean
)

/**
 * Data class representing a point with its distance and index on a line.
 *
 * @property point The [Point] on the line.
 * @property distance The distance from the reference point.
 * @property index The index of the point in the line.
 */
data class DistancePoint(
    val point: Point,
    val distance: Double,
    val index: Int,
)
