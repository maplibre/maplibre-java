package com.mapbox.geojson

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Feature.Companion.fromGeometry
import com.mapbox.geojson.FeatureCollection.Companion.fromFeature
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class FeatureCollectionTest : TestUtils() {
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val features: MutableList<Feature> = ArrayList()
        features.add(fromGeometry(null))
        features.add(fromGeometry(null))
        val featureCollection = fromFeatures(features)
        Assert.assertNotNull(featureCollection)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val features: MutableList<Feature> = ArrayList()
        features.add(fromGeometry(null))
        features.add(fromGeometry(null))
        val featureCollection = fromFeatures(features)
        Assert.assertNull(featureCollection.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val feature = fromGeometry(lineString)
        val features: MutableList<Feature> = ArrayList()
        features.add(feature)
        features.add(feature)
        val featureCollection = fromFeatures(features)
        compareJson(
            featureCollection.toJson(),
            "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]},"
                    + "\"properties\":{}},{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]},"
                    + "\"properties\":{}}]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val features: MutableList<Feature> = ArrayList()
        features.add(fromGeometry(null))
        features.add(fromGeometry(null))
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val featureCollection = fromFeatures(features, bbox)
        Assert.assertNotNull(featureCollection.bbox())
        Assert.assertEquals(1.0, featureCollection.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, featureCollection.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, featureCollection.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, featureCollection.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val feature = fromGeometry(lineString)
        val features: MutableList<Feature> = ArrayList()
        features.add(feature)
        features.add(feature)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val featureCollection = fromFeatures(features, bbox)
        compareJson(
            featureCollection.toJson(),
            "{\"type\":\"FeatureCollection\",\"bbox\":[1.0,2.0,3.0,4.0],"
                    + "\"features\":[{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]},\"properties\":{}},"
                    + "{\"type\":\"Feature\","
                    + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]},\"properties\":{}}"
                    + "]}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun passingInSingleFeature_doesHandleCorrectly() {
        val geometry = fromLngLat(1.0, 2.0)
        val feature = fromGeometry(geometry)
        val geo = fromFeature(feature)
        Assert.assertNotNull(geo.features())
        Assert.assertEquals(1, geo.features()!!.size.toLong())
        Assert.assertEquals(
            2.0,
            (geo.features()!![0].geometry() as Point?)!!.coordinates()[1],
            DELTA
        )
    }

    @Test
    @Throws(IOException::class)
    fun fromJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION)
        val geo = FeatureCollection.fromJson(json)
        Assert.assertEquals(geo.type(), "FeatureCollection")
        Assert.assertEquals(geo.features()!!.size.toLong(), 3)
        Assert.assertEquals(geo.features()!![0].type(), "Feature")
        Assert.assertEquals(geo.features()!![0].geometry()!!.type(), "Point")
        Assert.assertEquals(geo.features()!![1].type(), "Feature")
        Assert.assertEquals(geo.features()!![1].geometry()!!.type(), "LineString")
        Assert.assertEquals(geo.features()!![2].type(), "Feature")
        Assert.assertEquals(geo.features()!![2].geometry()!!.type(), "Polygon")
    }

    @Test
    @Throws(IOException::class)
    fun toJson() {
        val json = loadJsonFixture(SAMPLE_FEATURECOLLECTION_BBOX)
        val geo = FeatureCollection.fromJson(json)
        compareJson(json, geo.toJson())
    }

    companion object {
        private const val SAMPLE_FEATURECOLLECTION = "sample-featurecollection.json"
        private const val SAMPLE_FEATURECOLLECTION_BBOX = "sample-feature-collection-with-bbox.json"
    }
}