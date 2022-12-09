package com.mapbox.geojson.utils

import kotlin.math.roundToInt

/**
 * GeoJson utils class contains method that can be used throughout geojson package.
 *
 * @since 4.3.0
 */
object GeoJsonUtils {
    private const val ROUND_PRECISION = 10000000.0
    private const val MAX_DOUBLE_TO_ROUND = (Long.MAX_VALUE / ROUND_PRECISION).toLong()

    /**
     * Trims a double value to have only 7 digits after period.
     *
     * @param value to be trimed
     * @return trimmed value
     */
    @JvmStatic
    fun trim(value: Double): Double {
        return if (value > MAX_DOUBLE_TO_ROUND || value < -MAX_DOUBLE_TO_ROUND) {
            value
        } else (value * ROUND_PRECISION).roundToInt() / ROUND_PRECISION
    }
}