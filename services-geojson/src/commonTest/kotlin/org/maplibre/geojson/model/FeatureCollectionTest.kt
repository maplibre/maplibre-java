package org.maplibre.geojson.model

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.loadJsonFixture

class FeatureCollectionTest {

    @Test
    fun sanity() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val featureCollection = FeatureCollection(features)
        assertNotNull(featureCollection)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val featureCollection = FeatureCollection(features)
        assertNull(featureCollection.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)

        val features = listOf(feature, feature)

        val actualFeatureCollection = FeatureCollection.fromJson(FeatureCollection(features).toJson())
        val expectedFeatureCollection = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[" +
                "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}," +
                "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}]}")

        assertEquals(expectedFeatureCollection, actualFeatureCollection)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val features = listOf(
            Feature(),
            Feature()
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val featureCollection = FeatureCollection(features, bbox)
        assertNotNull(featureCollection.boundingBox)
        assertEquals(1.0, featureCollection.boundingBox!!.west, DELTA)
        assertEquals(2.0, featureCollection.boundingBox!!.south, DELTA)
        assertEquals(3.0, featureCollection.boundingBox!!.east, DELTA)
        assertEquals(4.0, featureCollection.boundingBox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val lineString = LineString(points)
        val feature = Feature(lineString)

        val features = listOf(feature, feature)
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val actualFeatureCollection = FeatureCollection.fromJson(FeatureCollection(features, bbox).toJson())
        val expectedFeatureCollection = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"bbox\":[1.0,2.0,3.0,4.0],"
                + "\"features\":[{\"type\":\"Feature\","
                + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}},"
                + "{\"type\":\"Feature\","
                + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}"
                + "]}")

        assertEquals(expectedFeatureCollection, actualFeatureCollection)
    }

    @Test
    fun passingInSingleFeature_doesHandleCorrectly() {
        val point = Point(1.0, 2.0)
        val feature = Feature(point)
        val geo = FeatureCollection(listOf(feature))
        assertNotNull(geo.features)
        assertEquals(1, geo.features.size)
        assertEquals(2.0, (geo.features.first().geometry as Point).latitude, DELTA)
    }

    @Test
    fun fromJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION)
        val geo = FeatureCollection.fromJson(json)
        assertEquals(geo.features.size, 3)
        assertTrue(geo.features[0].geometry is Point)
        assertTrue(geo.features[0].geometry is Point)
        assertTrue(geo.features[1].geometry is LineString)
        assertTrue(geo.features[2].geometry is Polygon)
    }

    @Test
    fun toJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION_BBOX)
        val expectedFeatureCollection = FeatureCollection.fromJson(json)
        val actualFeatureCollection = FeatureCollection.fromJson(FeatureCollection.fromJson(json).toJson())
        assertEquals(expectedFeatureCollection, actualFeatureCollection)
    }

    companion object {
        private const val SAMPLE_FEATURECOLLECTION = "sample-featurecollection.json"
        private const val SAMPLE_FEATURECOLLECTION_BBOX = "sample-feature-collection-with-bbox.json"
    }
}
