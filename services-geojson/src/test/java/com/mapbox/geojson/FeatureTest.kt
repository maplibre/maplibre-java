package com.mapbox.geojson

import com.google.gson.JsonObject
import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Feature.Companion.fromGeometry
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class FeatureTest : TestUtils() {
    @Test
    @Throws(Exception::class)
    fun sanity() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val feature = fromGeometry(lineString)
        Assert.assertNotNull(feature)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_nullWhenNotSet() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val feature = fromGeometry(lineString)
        Assert.assertNull(feature.bbox())
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val feature = fromGeometry(lineString)
        val featureJsonString = feature.toJson()
        compareJson(
            featureJsonString, "{\"type\":\"Feature\",\"geometry\":{\"type\":"
                    + "\"LineString\",\"coordinates\":[[1,2],[2,3]]}}"
        )
    }

    @Test
    @Throws(Exception::class)
    fun bbox_returnsCorrectBbox() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val feature = fromGeometry(lineString, bbox)
        Assert.assertNotNull(feature.bbox())
        Assert.assertEquals(1.0, feature.bbox()!!.west(), DELTA)
        Assert.assertEquals(2.0, feature.bbox()!!.south(), DELTA)
        Assert.assertEquals(3.0, feature.bbox()!!.east(), DELTA)
        Assert.assertEquals(4.0, feature.bbox()!!.north(), DELTA)
    }

    @Test
    @Throws(Exception::class)
    fun bbox_doesSerializeWhenPresent() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val feature = fromGeometry(lineString, bbox)
        val featureJsonString = feature.toJson()
        compareJson(
            "{\"type\":\"Feature\",\"bbox\":[1.0,2.0,3.0,4.0],\"geometry\":"
                    + "{\"type\":\"LineString\",\"coordinates\":[[1,2],[2,3]]}}",
            featureJsonString
        )
    }

    @Test
    @Throws(IOException::class)
    fun point_feature_fromJson() {
        val json = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"Point\", \"coordinates\": [ 125.6, 10.1] }," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"
        val geo = Feature.fromJson(json)
        Assert.assertEquals(geo.type(), "Feature")
        Assert.assertEquals(geo.geometry()!!.type(), "Point")
        Assert.assertEquals(
            (geo.geometry() as Point?)!!.longitude(),
            125.6,
            DELTA
        )
        Assert.assertEquals(
            (geo.geometry() as Point?)!!.latitude(),
            10.1,
            DELTA
        )
        Assert.assertEquals(geo.properties()["name"].asString, "Dinagat Islands")
    }

    @Test
    @Throws(IOException::class)
    fun linestring_feature_fromJson() {
        val json = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"LineString\", " +
                " \"coordinates\": [[ 102.0, 20],[103.0, 3.0],[104.0, 4.0], [105.0, 5.0]]}," +
                "\"properties\": {\"name\": \"line name\" }}"
        val geo = Feature.fromJson(json)
        Assert.assertEquals(geo.type(), "Feature")
        Assert.assertEquals(geo.geometry()!!.type(), "LineString")
        Assert.assertNotNull(geo.geometry())
        val coordinates = (geo.geometry() as LineString?)!!.coordinates()
        Assert.assertNotNull(coordinates)
        Assert.assertEquals(4, coordinates.size.toLong())
        Assert.assertEquals(105.0, coordinates[3].longitude(), DELTA)
        Assert.assertEquals(5.0, coordinates[3].latitude(), DELTA)
        Assert.assertEquals("line name", geo.properties()["name"].asString)
    }

    @Test
    @Throws(IOException::class)
    fun point_feature_toJson() {
        val properties = JsonObject()
        properties.addProperty("name", "Dinagat Islands")
        val geo = fromGeometry(
            fromLngLat(125.6, 10.1),
            properties
        )
        val geoJsonString = geo.toJson()
        val expectedJson = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"Point\", \"coordinates\": [ 125.6, 10.1] }," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"
        compareJson(expectedJson, geoJsonString)
    }

    @Test
    @Throws(IOException::class)
    fun linestring_feature_toJson() {
        val properties = JsonObject()
        properties.addProperty("name", "Dinagat Islands")
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 1.0))
        points.add(fromLngLat(2.0, 2.0))
        points.add(fromLngLat(3.0, 3.0))
        val lineString = LineString.fromLngLats(points)
        val geo = fromGeometry(lineString, properties)
        val geoJsonString = geo.toJson()
        val expectedJson = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"LineString\", \"coordinates\": [[1,1],[2,2],[3,3]]}," +
                "\"properties\": {\"name\": \"Dinagat Islands\" }}"
        compareJson(expectedJson, geoJsonString)
    }

    @Test
    fun testNullProperties() {
        val coordinates: MutableList<Point> = ArrayList()
        coordinates.add(fromLngLat(0.1, 2.3))
        coordinates.add(fromLngLat(4.5, 6.7))
        val line = LineString.fromLngLats(coordinates)
        val feature = fromGeometry(line)
        val jsonString = feature.toJson()
        Assert.assertFalse(jsonString!!.contains("\"properties\":{}"))

        // Feature (empty Properties) -> Json (null Properties) -> Equavalent Feature
        val featureFromJson = Feature.fromJson(jsonString)
        Assert.assertEquals(featureFromJson, feature)
    }

    @Test
    fun testNonNullProperties() {
        val coordinates: MutableList<Point> = ArrayList()
        coordinates.add(fromLngLat(0.1, 2.3))
        coordinates.add(fromLngLat(4.5, 6.7))
        val line = LineString.fromLngLats(coordinates)
        val properties = JsonObject()
        properties.addProperty("key", "value")
        val feature = fromGeometry(line, properties)
        val jsonString = feature.toJson()
        Assert.assertTrue(jsonString!!.contains("\"properties\":{\"key\":\"value\"}"))

        // Feature (non-empty Properties) -> Json (non-empty Properties) -> Equavalent Feature
        Assert.assertEquals(Feature.fromJson(jsonString), feature)
    }

    @Test
    fun testNullPropertiesJson() {
        val jsonString = ("{\"type\":\"Feature\"," +
                " \"bbox\":[1.0,2.0,3.0,4.0]," +
                " \"geometry\":"
                + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}")
        val feature = Feature.fromJson(jsonString)

        // Json( null Properties) -> Feature (empty Properties) -> Json(null Properties)
        val fromFeatureJsonString = feature.toJson()
        compareJson(fromFeatureJsonString, jsonString)
    }

    @Test
    @Throws(IOException::class)
    fun pointFeature_fromJson_toJson() {
        val jsonString = "{\"id\" : \"id0\"," +
                " \"bbox\": [-120.0, -60.0, 120.0, 60.0]," +
                " \"geometry\": {" +
                "    \"bbox\": [-110.0, -50.0, 110.0, 50.0]," +
                "    \"coordinates\": [ 100.0, 0.0], " +
                "     \"type\": \"Point\"}," +
                "\"type\": \"Feature\"," +
                "\"properties\": {\"prop0\": \"value0\", \"prop1\": \"value1\"}" +
                "}"
        val featureFromJson = Feature.fromJson(jsonString)
        val jsonStringFromFeature = featureFromJson.toJson()
        compareJson(jsonString, jsonStringFromFeature)
    }

    @Test
    @Throws(IOException::class)
    fun feature_getProperty_empty_property() {
        val jsonString = ("{\"type\":\"Feature\"," +
                " \"geometry\":"
                + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}}")
        val feature = Feature.fromJson(jsonString)
        var value: Any? = feature.getStringProperty("does_not_exist")
        Assert.assertNull(value)
        value = feature.getBooleanProperty("does_not_exist")
        Assert.assertNull(value)
        value = feature.getNumberProperty("does_not_exist")
        Assert.assertNull(value)
    }

    @Test
    @Throws(IOException::class)
    fun feature_property_doesnotexist() {
        val jsonString = "{ \"type\": \"Feature\"," +
                "\"geometry\": { \"type\": \"LineString\", \"coordinates\": [[1,1],[2,2],[3,3]]}," +
                "\"properties\": {\"some_name\": \"some_value\" }}"
        val feature = Feature.fromJson(jsonString)
        var value: Any? = feature.getStringProperty("does_not_exist")
        Assert.assertNull(value)
        value = feature.getBooleanProperty("does_not_exist")
        Assert.assertNull(value)
        value = feature.getNumberProperty("does_not_exist")
        Assert.assertNull(value)
    }

}