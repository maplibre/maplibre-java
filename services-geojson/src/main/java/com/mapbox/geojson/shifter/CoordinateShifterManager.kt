package com.mapbox.geojson.shifter

import com.mapbox.geojson.Point

/**
 * CoordinateShifterManager keeps track of currently set CoordinateShifter.
 *
 * @since 4.2.0
 */
@Suppress("unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused", "unused",
    "unused", "unused", "unused", "unused"
)
object CoordinateShifterManager {
    private val DEFAULT: CoordinateShifter = object : CoordinateShifter {
        override fun shiftLonLat(lon: Double, lat: Double): List<Double> {
            return listOf(lon, lat)
        }

        override fun shiftLonLatAlt(lon: Double, lat: Double, altitude: Double): List<Double> {
            return if (java.lang.Double.isNaN(altitude)) listOf(lon, lat) else listOf(
                lon,
                lat,
                altitude
            )
        }

        override fun unshiftPoint(shiftedPoint: Point): List<Double?> {
            return shiftedPoint.coordinates()
        }

        override fun unshiftPoint(shiftedCoordinates: List<Double>): List<Double> {
            return shiftedCoordinates
        }
    }

    @Volatile
    private var coordinateShifter = DEFAULT

    /**
     * Currently set CoordinateShifterManager.
     *
     * @return Currently set CoordinateShifterManager
     * @since 4.2.0
     */
    fun getCoordinateShifter(): CoordinateShifter {
        return coordinateShifter
    }

    /**
     * Sets CoordinateShifterManager.
     *
     * @param coordinateShifter CoordinateShifterManager to be set
     * @since 4.2.0
     */
    @JvmStatic
    fun setCoordinateShifter(coordinateShifter: CoordinateShifter?) {
        CoordinateShifterManager.coordinateShifter = coordinateShifter ?: DEFAULT
    }

    /**
     * Check whether the current shifter is the default one.
     * @return true if using default shifter.
     */
    @JvmStatic
    val isUsingDefaultShifter: Boolean
        get() = coordinateShifter === DEFAULT
}