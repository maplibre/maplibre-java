package org.maplibre.turf.common

import org.maplibre.geojson.turf.TurfUnit
import org.maplibre.turf.TurfConstants
import org.maplibre.turf.TurfException
import org.maplibre.geojson.turf.TurfException as CommonTurfException

fun String.toUnit(): TurfUnit {
    return when (this) {
        TurfConstants.UNIT_MILES -> TurfUnit.MILES
        TurfConstants.UNIT_NAUTICAL_MILES -> TurfUnit.NAUTICAL_MILES
        TurfConstants.UNIT_KILOMETERS -> TurfUnit.KILOMETERS
        TurfConstants.UNIT_RADIANS -> TurfUnit.RADIANS
        TurfConstants.UNIT_DEGREES -> TurfUnit.DEGREES
        TurfConstants.UNIT_INCHES -> TurfUnit.INCHES
        TurfConstants.UNIT_YARDS -> TurfUnit.YARDS
        TurfConstants.UNIT_METERS -> TurfUnit.METERS
        TurfConstants.UNIT_CENTIMETERS -> TurfUnit.CENTIMETERS
        TurfConstants.UNIT_FEET -> TurfUnit.FEET
        TurfConstants.UNIT_CENTIMETRES -> TurfUnit.CENTIMETRES
        TurfConstants.UNIT_METRES -> TurfUnit.METRES
        TurfConstants.UNIT_KILOMETRES -> TurfUnit.KILOMETERS
        else -> throw IllegalArgumentException("Invalid unit")
    }
}

fun Exception.toJvm(): Exception {
    return when (this) {
        is CommonTurfException -> TurfException(message)
        else -> this
    }
}