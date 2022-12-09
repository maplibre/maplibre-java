package com.mapbox.turf

import androidx.annotation.FloatRange
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.turf.TurfConstants.TurfUnitCriteria
import com.mapbox.turf.models.LineIntersectsResult

/**
 * Class contains all the miscellaneous methods that Turf can perform.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
class TurfMisc private constructor() {
    init {
        throw AssertionError("No Instances.")
    }

    companion object {
        private const val INDEX_KEY = "index"

        /**
         * Takes a line, a start [Point], and a stop point and returns the line in between those
         * points.
         *
         * @param startPt Starting point.
         * @param stopPt  Stopping point.
         * @param line    Line to slice.
         * @return Sliced line.
         * @throws TurfException signals that a Turf exception of some sort has occurred.
         * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslice)
         *
         * @since 1.2.0
         */
        @kotlin.jvm.JvmStatic
        fun lineSlice(
            startPt: Point, stopPt: Point,
            line: Feature
        ): LineString {
            if (line.geometry() == null) {
                throw NullPointerException("Feature.geometry() == null")
            }
            if (line.geometry()!!.type() != "LineString") {
                throw TurfException("input must be a LineString Feature or Geometry")
            }
            return lineSlice(startPt, stopPt, (line.geometry() as LineString?)!!)
        }

        /**
         * Takes a line, a start [Point], and a stop point and returns the line in between those
         * points.
         *
         * @param startPt used for calculating the lineSlice
         * @param stopPt  used for calculating the lineSlice
         * @param line    geometry that should be sliced
         * @return a sliced [LineString]
         * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslice)
         *
         * @since 1.2.0
         */
        @JvmStatic
        fun lineSlice(
            startPt: Point, stopPt: Point,
            line: LineString
        ): LineString {
            val coords: List<Point?> = line.coordinates()
            if (coords.size < 2) {
                throw TurfException(
                    "Turf lineSlice requires a LineString made up of at least 2 "
                            + "coordinates."
                )
            } else if (startPt.equals(stopPt)) {
                throw TurfException("Start and stop points in Turf lineSlice cannot equal each other.")
            }
            val startVertex = nearestPointOnLine(startPt, coords)
            val stopVertex = nearestPointOnLine(stopPt, coords)
            val ends: MutableList<Feature> = ArrayList()
            if (startVertex.getNumberProperty(INDEX_KEY) as Int
                <= stopVertex.getNumberProperty(INDEX_KEY) as Int
            ) {
                ends.add(startVertex)
                ends.add(stopVertex)
            } else {
                ends.add(stopVertex)
                ends.add(startVertex)
            }
            val points: MutableList<Point> = ArrayList()
            points.add(ends[0].geometry() as Point)
            for (i in ends[0].getNumberProperty(INDEX_KEY) as Int + 1 until ends[1].getNumberProperty(
                INDEX_KEY
            ) as Int + 1) {
                points.add(coords[i]!!)
            }
            points.add(ends[1].geometry() as Point)
            return LineString.fromLngLats(points)
        }

        /**
         * Takes a [LineString], a specified distance along the line to a start [Point],
         * and a specified distance along the line to a stop point
         * and returns a subsection of the line in-between those points.
         *
         *
         * This can be useful for extracting only the part of a route between two distances.
         *
         * @param line input line
         * @param startDist distance along the line to starting point
         * @param stopDist distance along the line to ending point
         * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
         * can be degrees, radians, miles, or kilometers
         * @return sliced line
         * @throws TurfException signals that a Turf exception of some sort has occurred.
         * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslicealong)
         *
         * @since 3.1.0
         */
        @kotlin.jvm.JvmStatic
        fun lineSliceAlong(
            line: Feature,
            @FloatRange(from = 0.0) startDist: Double,
            @FloatRange(from = 0.0) stopDist: Double,
            @TurfUnitCriteria units: String
        ): LineString {
            if (line.geometry() == null) {
                throw NullPointerException("Feature.geometry() == null")
            }
            if (line.geometry()!!.type() != "LineString") {
                throw TurfException("input must be a LineString Feature or Geometry")
            }
            return lineSliceAlong((line.geometry() as LineString?)!!, startDist, stopDist, units)
        }

        /**
         *
         *
         * Takes a [LineString], a specified distance along the line to a start [Point],
         * and a specified distance along the line to a stop point,
         * returns a subsection of the line in-between those points.
         *
         *
         * This can be useful for extracting only the part of a route between two distances.
         *
         * @param line input line
         * @param startDist distance along the line to starting point
         * @param stopDist distance along the line to ending point
         * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
         * can be degrees, radians, miles, or kilometers
         * @return sliced line
         * @throws TurfException signals that a Turf exception of some sort has occurred.
         * @see [Turf Line slice documentation](http://turfjs.org/docs/.lineslicealong)
         *
         * @since 3.1.0
         */
        @JvmStatic
        fun lineSliceAlong(
            line: LineString,
            @FloatRange(from = 0.0) startDist: Double,
            @FloatRange(from = 0.0) stopDist: Double,
            @TurfUnitCriteria units: String
        ): LineString {
            val coords = line.coordinates()
            if (coords.size < 2) {
                throw TurfException(
                    "Turf lineSlice requires a LineString Geometry made up of "
                            + "at least 2 coordinates. The LineString passed in only contains " + coords.size + "."
                )
            } else if (startDist == stopDist) {
                throw TurfException(
                    "Start and stop distance in Turf lineSliceAlong "
                            + "cannot equal each other."
                )
            }
            val slice: MutableList<Point> = ArrayList(2)
            var travelled = 0.0
            for (i in coords.indices) {
                if (startDist >= travelled && i == coords.size - 1) {
                    break
                } else if (travelled > startDist && slice.size == 0) {
                    val overshot = startDist - travelled
                    if (overshot == 0.0) {
                        slice.add(coords[i])
                        return LineString.fromLngLats(slice)
                    }
                    val direction: Double =
                        TurfMeasurement.Companion.bearing(coords[i], coords[i - 1]) - 180
                    val interpolated: Point = TurfMeasurement.Companion.destination(
                        coords[i], overshot, direction, units
                    )
                    slice.add(interpolated)
                }
                if (travelled >= stopDist) {
                    val overshot = stopDist - travelled
                    if (overshot == 0.0) {
                        slice.add(coords[i])
                        return LineString.fromLngLats(slice)
                    }
                    val direction: Double =
                        TurfMeasurement.Companion.bearing(coords[i], coords[i - 1]) - 180
                    val interpolated: Point = TurfMeasurement.Companion.destination(
                        coords[i], overshot, direction, units
                    )
                    slice.add(interpolated)
                    return LineString.fromLngLats(slice)
                }
                if (travelled >= startDist) {
                    slice.add(coords[i])
                }
                if (i == coords.size - 1) {
                    return LineString.fromLngLats(slice)
                }
                travelled += TurfMeasurement.Companion.distance(coords[i], coords[i + 1], units)
            }
            if (travelled < startDist) {
                throw TurfException("Start position is beyond line")
            }
            return LineString.fromLngLats(slice)
        }
        /**
         * Takes a [Point] and a [LineString] and calculates the closest Point on the
         * LineString.
         *
         * @param pt point to snap from
         * @param coords line to snap to
         * @param units one of the units found inside [TurfConstants.TurfUnitCriteria]
         * can be degrees, radians, miles, or kilometers
         * @return closest point on the line to point
         * @since 4.9.0
         */
        /**
         * Takes a [Point] and a [LineString] and calculates the closest Point on the
         * LineString.
         *
         * @param pt     point to snap from
         * @param coords line to snap to
         * @return closest point on the line to point
         * @since 1.3.0
         */
        @JvmStatic
        @JvmOverloads
        fun nearestPointOnLine(
            pt: Point, coords: List<Point?>,
            @TurfUnitCriteria units: String? = null
        ): Feature {
            var units = units
            if (coords.size < 2) {
                throw TurfException(
                    "Turf nearestPointOnLine requires a List of Points "
                            + "made up of at least 2 coordinates."
                )
            }
            if (units == null) {
                units = TurfConstants.UNIT_KILOMETERS
            }
            var closestPt = Feature.fromGeometry(
                fromLngLat(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
            )
            closestPt.addNumberProperty("dist", Double.POSITIVE_INFINITY)
            for (i in 0 until coords.size - 1) {
                val start = Feature.fromGeometry(coords[i])
                val stop = Feature.fromGeometry(coords[i + 1])
                //start
                start.addNumberProperty(
                    "dist", TurfMeasurement.Companion.distance(
                        pt, (start.geometry() as Point?)!!, units
                    )
                )
                //stop
                stop.addNumberProperty(
                    "dist", TurfMeasurement.Companion.distance(
                        pt, (stop.geometry() as Point?)!!, units
                    )
                )
                //perpendicular
                val heightDistance = Math.max(
                    start.properties()["dist"].asDouble,
                    stop.properties()["dist"].asDouble
                )
                val direction: Double = TurfMeasurement.Companion.bearing(
                    (start.geometry() as Point?)!!,
                    (stop.geometry() as Point?)!!
                )
                val perpendicularPt1 = Feature.fromGeometry(
                    TurfMeasurement.Companion.destination(pt, heightDistance, direction + 90, units)
                )
                val perpendicularPt2 = Feature.fromGeometry(
                    TurfMeasurement.Companion.destination(pt, heightDistance, direction - 90, units)
                )
                val intersect = lineIntersects(
                    (perpendicularPt1.geometry() as Point?)!!.longitude(),
                    (perpendicularPt1.geometry() as Point?)!!.latitude(),
                    (perpendicularPt2.geometry() as Point?)!!.longitude(),
                    (perpendicularPt2.geometry() as Point?)!!.latitude(),
                    (start.geometry() as Point?)!!.longitude(),
                    (start.geometry() as Point?)!!.latitude(),
                    (stop.geometry() as Point?)!!.longitude(),
                    (stop.geometry() as Point?)!!.latitude()
                ) 
                var intersectPt: Feature? = null
                if (intersect != null) {
                    intersectPt = Feature.fromGeometry(
                        fromLngLat(
                            intersect.horizontalIntersection()!!,
                            intersect.verticalIntersection()!!
                        )
                    )
                    intersectPt.addNumberProperty(
                        "dist", TurfMeasurement.Companion.distance(
                            pt,
                            (intersectPt.geometry() as Point?)!!, units
                        )
                    )
                }
                if ((start.getNumberProperty("dist") as Double) < closestPt.getNumberProperty("dist") as Double){
                    closestPt = start
                    closestPt.addNumberProperty(INDEX_KEY, i)
                }
                if ((stop.getNumberProperty("dist") as Double) < closestPt.getNumberProperty("dist") as Double){
                    closestPt = stop
                    closestPt.addNumberProperty(INDEX_KEY, i)
                }
                if ((intersectPt != null && (intersectPt.getNumberProperty("dist") as Double) < closestPt.getNumberProperty("dist") as Double
                )){
                    closestPt = intersectPt
                    closestPt.addNumberProperty(INDEX_KEY, i)
                }
            }
            return closestPt
        }

        private fun lineIntersects(
            line1StartX: Double, line1StartY: Double,
            line1EndX: Double, line1EndY: Double,
            line2StartX: Double, line2StartY: Double,
            line2EndX: Double, line2EndY: Double
        ): LineIntersectsResult? {
            // If the lines intersect, the result contains the x and y of the intersection
            // (treating the lines as infinite) and booleans for whether line segment 1 or line
            // segment 2 contain the point
            var result: LineIntersectsResult? = LineIntersectsResult.Companion.builder()
                .onLine1(false)
                .onLine2(false)
                .build()
            val denominator = ((line2EndY - line2StartY) * (line1EndX - line1StartX)
                    - (line2EndX - line2StartX) * (line1EndY - line1StartY))
            if (denominator == 0.0) {
                return if (result!!.horizontalIntersection() != null && result.verticalIntersection() != null) {
                    result
                } else {
                    null
                }
            }
            var varA = line1StartY - line2StartY
            var varB = line1StartX - line2StartX
            val numerator1 = (line2EndX - line2StartX) * varA - (line2EndY - line2StartY) * varB
            val numerator2 = (line1EndX - line1StartX) * varA - (line1EndY - line1StartY) * varB
            varA = numerator1 / denominator
            varB = numerator2 / denominator

            // if we cast these lines infinitely in both directions, they intersect here:
            result = result!!.toBuilder().horizontalIntersection(
                line1StartX
                        + varA * (line1EndX - line1StartX)
            ).build()
            result = result.toBuilder().verticalIntersection(
                line1StartY
                        + varA * (line1EndY - line1StartY)
            ).build()

            // if line1 is a segment and line2 is infinite, they intersect if:
            if (varA > 0 && varA < 1) {
                result = result.toBuilder().onLine1(true).build()
            }
            // if line2 is a segment and line1 is infinite, they intersect if:
            if (varB > 0 && varB < 1) {
                result = result.toBuilder().onLine2(true).build()
            }
            // if line1 and line2 are segments, they intersect if both of the above are true
            return if (result.onLine1() && result.onLine2()) {
                result
            } else {
                null
            }
        }
    }
}