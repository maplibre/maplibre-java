package com.mapbox.geojson.utils

import com.mapbox.geojson.TestUtils
import com.mapbox.geojson.utils.GeoJsonUtils.trim
import org.junit.Assert
import org.junit.Test

class GeoJsonUtilsTest : TestUtils() {
    @Test
    fun trimPositiveRoundUp() {
        val trimmedValue = trim(3.123456789)
        val expected = 3.1234568
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }

    @Test
    fun trimPositiveRoundDown() {
        val trimmedValue = trim(3.123456712)
        val expected = 3.1234567
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }

    @Test
    fun trimNegative() {
        val trimmedValue = trim(-3.123456789)
        val expected = -3.1234568
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }

    @Test
    fun trimZero() {
        val trimmedValue = trim(0.0)
        val expected = 0.0
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }

    @Test
    fun trimInt() {
        val trimmedValue = trim(8.0)
        val expected = 8.0
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }

    @Test
    fun trimMaxLong() {
        val trimmedValue = trim(Long.MAX_VALUE + 0.1)
        val expected = Long.MAX_VALUE.toDouble()
        Assert.assertEquals("trim to 7 digits after period", expected, trimmedValue, 1e-8)
    }
}