package com.mapbox.turf

import androidx.annotation.IntRange
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.turf.TurfConstants.TurfUnitCriteria

/**
 * Methods in this class consume one GeoJSON object and output a new object with the defined
 * parameters provided.
 *
 * @since 3.0.0
 */
object TurfTransformation {
    private const val DEFAULT_STEPS = 64

    /**
     * Takes a [Point] and calculates the circle polygon given a radius in the
     * provided [TurfConstants.TurfUnitCriteria]; and steps for precision. This
     * method uses the [.DEFAULT_STEPS].
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    @kotlin.jvm.JvmStatic
    fun circle(
        center: Point, radius: Double,
        @TurfUnitCriteria units: String?
    ): Polygon {
        return circle(center, radius, DEFAULT_STEPS, units)
    }
    /**
     * Takes a [Point] and calculates the circle polygon given a radius in the
     * provided [TurfConstants.TurfUnitCriteria]; and steps for precision.
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @param steps  number of steps which make up the circle parameter
     * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    /**
     * Takes a [Point] and calculates the circle polygon given a radius in degrees, radians,
     * miles, or kilometers; and steps for precision. This uses the [.DEFAULT_STEPS] and
     * [TurfConstants.UNIT_DEFAULT] values.
     *
     * @param center a [Point] which the circle will center around
     * @param radius the radius of the circle
     * @return a [Polygon] which represents the newly created circle
     * @since 3.0.0
     */
    @JvmOverloads
    @kotlin.jvm.JvmStatic
    fun circle(
        center: Point, radius: Double, @IntRange(from = 1) steps: Int = 64,
        @TurfUnitCriteria units: String? = TurfConstants.UNIT_DEFAULT
    ): Polygon {
        val coordinates: MutableList<Point> = ArrayList()
        for (i in 0 until steps) {
            coordinates.add(
                TurfMeasurement.Companion.destination(
                    center,
                    radius,
                    i * 360.0 / steps,
                    units!!
                )
            )
        }
        if (coordinates.size > 0) {
            coordinates.add(coordinates[0])
        }
        val coordinate: MutableList<List<Point>> = ArrayList()
        coordinate.add(coordinates)
        return fromLngLats(coordinate)
    }
}