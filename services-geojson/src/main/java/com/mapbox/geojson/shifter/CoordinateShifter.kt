package com.mapbox.geojson.shifter

import com.mapbox.geojson.Point

/**
 * ShifterManager allows the movement of all Point objects according to a custom algorithm.
 * Once set, it will be applied to all Point objects created through this method.
 *
 * @since 4.2.0
 */
interface CoordinateShifter {
    /**
     * Shifted coordinate values according to its algorithm.
     *
     * @param lon unshifted longitude
     * @param lat unshifted latitude
     * @return shifted longitude, shifted latitude in the form of a List of Double values
     * @since 4.2.0
     */
    fun shiftLonLat(lon: Double, lat: Double): List<Double>

    /**
     * Shifted coordinate values according to its algorithm.
     *
     * @param lon unshifted longitude
     * @param lat unshifted latitude
     * @param altitude  unshifted altitude
     * @return shifted longitude, shifted latitude, shifted altitude in the form of a
     * List of Double values
     * @since 4.2.0
     */
    fun shiftLonLatAlt(lon: Double, lat: Double, altitude: Double): List<Double>

    /**
     * Unshifted coordinate values according to its algorithm.
     *
     * @param shiftedPoint shifted point
     * @return unshifted longitude, shifted latitude,
     * and altitude (if present) in the form of List of Double
     * @since 4.2.0
     */
    fun unshiftPoint(shiftedPoint: Point): List<Double?>

    /**
     * Unshifted coordinate values according to its algorithm.
     *
     * @param shiftedCoordinates shifted point
     * @return unshifted longitude, shifted latitude,
     * and altitude (if present) in the form of List of Double
     * @since 4.2.0
     */
    fun unshiftPoint(shiftedCoordinates: List<Double>): List<Double>
}