package org.maplibre.geojson.util

object TestUtils {

    fun loadJsonFixture(filename: String): String {
        return readResourceFile(filename)
    }

    const val DELTA: Double = 1E-2
}
