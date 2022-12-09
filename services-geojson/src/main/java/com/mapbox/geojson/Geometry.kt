package com.mapbox.geojson

import androidx.annotation.Keep

/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Keep
interface Geometry : GeoJson