package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfAssertions.collectionOf
import com.mapbox.turf.TurfAssertions.featureOf
import com.mapbox.turf.TurfAssertions.geojsonType
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class TurfAssertionsTest : TestUtils() {
    @Rule
    var thrown = ExpectedException.none()
    @Test
    @Throws(TurfException::class)
    fun testInvariantGeojsonType1() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Type and name required"))
        geojsonType(null, null, null)
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantGeojsonType2() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Type and name required"))
        geojsonType(null, null, "myfn")
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantGeojsonType3() {
        val json = "{ type: 'Point', coordinates: [0, 0] }"
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Invalid input to myfn: must be a Polygon, given Point"))
        geojsonType(Point.fromJson(json), "Polygon", "myfn")
    }

    @Test
    fun testInvariantGeojsonType4() {
        val json = "{ type: 'Point', coordinates: [0, 0] }"
        geojsonType(Point.fromJson(json), "Point", "myfn")
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantFeatureOf1() {
        val json = ("{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0] }, "
                + "properties: {}}")
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith(".featureOf() requires a name"))
        featureOf(Feature.fromJson(json), "Polygon", null)
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantFeatureOf2() {
        val json = "{ type: 'Feature'}"
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Invalid input to foo, Feature with geometry required"))
        featureOf(Feature.fromJson(json), "Polygon", "foo")
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantFeatureOf3() {
        val json = "{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0] }}"
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Invalid input to myfn: must be a Polygon, given Point"))
        featureOf(Feature.fromJson(json), "Polygon", "myfn")
    }

    @Test
    fun testInvariantFeatureOf4() {
        val json = ("{ type: 'Feature', geometry: { type: 'Point', coordinates: [0, 0]}, "
                + "properties: {}}")
        featureOf(Feature.fromJson(json), "Point", "myfn")
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantCollectionOf1() {
        val json = ("{type: 'FeatureCollection', features: [{ type: 'Feature', geometry: { "
                + "type: 'Point', coordinates: [0, 0]}, properties: {}}]}")
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Invalid input to myfn: must be a Polygon, given Point"))
        collectionOf(FeatureCollection.fromJson(json), "Polygon", "myfn")
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantCollectionOf2() {
        val json = "{type: 'FeatureCollection'}"
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("collectionOf() requires a name"))
        collectionOf(FeatureCollection.fromJson(json), "Polygon", null)
    }

    @Test
    @Throws(TurfException::class)
    fun testInvariantCollectionOf3() {
        val json = "{type: 'FeatureCollection'}"
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Invalid input to foo, FeatureCollection required"))
        collectionOf(FeatureCollection.fromJson(json), "Polygon", "foo")
    }

    @Test
    fun testInvariantCollectionOf4() {
        val json = ("{type: 'FeatureCollection', features: [{ type: 'Feature', geometry: { "
                + "type: 'Point', coordinates: [0, 0]}, properties: {}}]}")
        collectionOf(FeatureCollection.fromJson(json), "Point", "myfn")
    }
}