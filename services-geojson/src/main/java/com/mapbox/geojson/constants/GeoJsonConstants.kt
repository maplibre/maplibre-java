package com.mapbox.geojson.constants

/**
 * Contains constants used throughout the GeoJson classes.
 *
 * @since 3.0.0
 */
object GeoJsonConstants {
    /**
     * A Mercator project has a finite longitude values, this constant represents the lowest value
     * available to represent a geolocation.
     *
     * @since 3.0.0
     */
    const val MIN_LONGITUDE = -180.0

    /**
     * A Mercator project has a finite longitude values, this constant represents the highest value
     * available to represent a geolocation.
     *
     * @since 3.0.0
     */
    const val MAX_LONGITUDE = 180.0

    /**
     * While on a Mercator projected map the width (longitude) has a finite values, the height
     * (latitude) can be infinitely long. This constant restrains the lower latitude value to -90 in
     * order to preserve map readability and allows easier logic for tile selection.
     *
     * @since 3.0.0
     */
    const val MIN_LATITUDE = -90.0

    /**
     * While on a Mercator projected map the width (longitude) has a finite values, the height
     * (latitude) can be infinitely long. This constant restrains the upper latitude value to 90 in
     * order to preserve map readability and allows easier logic for tile selection.
     *
     * @since 3.0.0
     */
    const val MAX_LATITUDE = 90.0
}