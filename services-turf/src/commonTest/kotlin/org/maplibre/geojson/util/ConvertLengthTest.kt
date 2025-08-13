package org.maplibre.geojson.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ConvertLengthTest {

    @Test
    fun `Convert meters to kilometers`() {
        val kilometers = convertLength(1000.0, MeasureUnit.METERS, MeasureUnit.KILOMETERS)
        assertEquals(1.0, kilometers)
    }

    @Test
    fun `Convert kilometers to miles`() {
        val miles = convertLength(1.0, MeasureUnit.KILOMETERS, MeasureUnit.MILES)
        assertEquals(0.6213714106386318, miles)
    }

    @Test
    fun `Convert miles to kilometers`() {
        val kilometers = convertLength(1.0, MeasureUnit.MILES, MeasureUnit.KILOMETERS)
        assertEquals(1.6093434343434343, kilometers)
    }

    @Test
    fun `Convert nautical miles to kilometers`() {
        val nauticalMiles = convertLength(1.0, MeasureUnit.NAUTICAL_MILES, MeasureUnit.KILOMETERS)
        assertEquals(1.851999843075488, nauticalMiles)
    }

    @Test
    fun `Convert meters miles to centimeters`() {
        val centimeters = convertLength(1.0, MeasureUnit.METERS, MeasureUnit.CENTIMETERS)
        assertEquals(100.0, centimeters)
    }
}