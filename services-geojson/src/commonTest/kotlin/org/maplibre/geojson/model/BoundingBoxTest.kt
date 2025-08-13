package org.maplibre.geojson.model

import kotlinx.serialization.json.Json
import org.maplibre.geojson.TestUtils.DELTA
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BoundingBoxTest {

    @Test
    fun sanity() {
        val southwest = Point(2.0, 2.0)
        val northeast = Point(4.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertNotNull(boundingBox)
    }

    @Test
    fun southWest_doesReturnMostSouthwestCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(southwest, boundingBox.southwest)
    }

    @Test
    fun northEast_doesReturnMostNortheastCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(northeast, boundingBox.northeast)
    }

    @Test
    fun west_doesReturnMostWestCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(1.0, boundingBox.west, DELTA)
    }

    @Test
    fun south_doesReturnMostSouthCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(2.0, boundingBox.south, DELTA)
    }

    @Test
    fun east_doesReturnMostEastCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(3.0, boundingBox.east, DELTA)
    }

    @Test
    fun north_doesReturnMostNorthCoordinate() {
        val southwest = Point(1.0, 2.0)
        val northeast = Point(3.0, 4.0)
        val boundingBox = BoundingBox(southwest, northeast)
        assertEquals(4.0, boundingBox.north, DELTA)
    }

    @Test
    fun `JSON serializing without altitude`() {
        val boundingBox = BoundingBox(Point(1.1, 2.2), Point(3.3, 4.4))
        val json = boundingBox.toJson()

        assertEquals(Json.parseToJsonElement(json), Json.parseToJsonElement("[1.1, 2.2, 3.3, 4.4]"))
    }

    @Test
    fun `JSON serializing with altitude`() {
        val boundingBox = BoundingBox(Point(1.1, 2.2, 3.3), Point(4.4, 5.5, 6.6))
        val json = boundingBox.toJson()

        assertEquals(Json.parseToJsonElement(json), Json.parseToJsonElement("[1.1, 2.2, 3.3, 4.4, 5.5, 6.6]"))
    }

    @Test
    fun `JSON deserializing without altitude`() {
        val json = "[1.0, 2.0, 3.0, 4.0]"
        val boundingBox = BoundingBox.fromJson(json)

        assertEquals(BoundingBox(Point(1.0, 2.0), Point(3.0, 4.0)), boundingBox)
    }

    @Test
    fun `JSON deserializing with altitude`() {
        val json = "[1.0, 2.0, 3.0, 4.0, 5.0, 6.0]"
        val boundingBox = BoundingBox.fromJson(json)

        assertEquals(BoundingBox(Point(1.0, 2.0, 3.0), Point(4.0, 5.0, 6.0)), boundingBox)
    }

    @Test
    fun `JSON deserializing with wrong array size`() {
        val json = "[1.0, 2.0, 3.0, 4.0, 5.0]"

        assertFailsWith(IllegalArgumentException::class) {
            BoundingBox.fromJson(json)
        }
    }
}
