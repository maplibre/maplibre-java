package org.maplibre.geojson.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test
import org.maplibre.geojson.TestUtils.DELTA
import org.maplibre.geojson.TestUtils.compareJson

class FeatureTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)
        assertNotNull(feature)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)
        assertNull(feature.boundingBox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val feature = Feature(lineString)

        val actualFeature = Feature.fromJson(feature.toJson())
        val expectedFeature = Feature.fromJson(
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [1.0, 2.0],
                        [2.0, 3.0]
                    ]
                }
            }
            """.trimIndent()
        )

        assertEquals(expectedFeature, actualFeature)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val feature = Feature(lineString, boundingBox = bbox)
        assertNotNull(feature.boundingBox)
        assertEquals(1.0, feature.boundingBox!!.west, DELTA)
        assertEquals(2.0, feature.boundingBox!!.south, DELTA)
        assertEquals(3.0, feature.boundingBox!!.east, DELTA)
        assertEquals(4.0, feature.boundingBox!!.north, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val feature = Feature(lineString, boundingBox = bbox)

        val actualFeature = Feature.fromJson(feature.toJson())
        val expectedFeature = Feature.fromJson(
            """
            {
                "type": "Feature",
                "bbox": [1.0, 2.0, 3.0, 4.0],
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [1.0, 2.0],
                        [2.0, 3.0]
                    ]
                }
            }
            """.trimIndent()
        )

        assertEquals(expectedFeature, actualFeature)
    }

    @Test
    fun point_feature_fromJson() {
        val json =
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "Point",
                    "coordinates": [125.6, 10.1]
                },
                "properties": {
                    "name": "Dinagat Islands"
                }
            }
            """.trimIndent()
        val feature = Feature.fromJson(json)
        assertEquals((feature.geometry as Point).longitude, 125.6, DELTA)
        assertEquals((feature.geometry as Point).latitude, 10.1, DELTA)
        assertEquals(
            feature.properties!!.jsonObject["name"]!!.jsonPrimitive.content,
            "Dinagat Islands"
        )
    }

    @Test
    fun linestring_feature_fromJson() {
        val json =
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [102.0, 20.0],
                        [103.0, 3.0],
                        [104.0, 4.0],
                        [105.0, 5.0]
                    ]
                },
                "properties": {
                    "name": "line name"
                }
            }
            """.trimIndent()
        val feature = Feature.fromJson(json)
        assertNotNull(feature.geometry)
        val points = (feature.geometry as LineString).points
        assertNotNull(points)
        assertEquals(4, points.size.toLong())
        assertEquals(105.0, points[3].longitude, DELTA)
        assertEquals(5.0, points[3].latitude, DELTA)
        assertEquals("line name", feature.properties!!.jsonObject["name"]!!.jsonPrimitive.content)
    }

    @Test
    fun point_feature_toJson() {
        val properties = JsonObject(
            content = mapOf(
                "name" to JsonPrimitive("Dinagat Islands")
            )
        )
        val geo = Feature(
            Point(125.6, 10.1),
            properties = properties
        )
        val geoJsonString = geo.toJson()

        val expectedJson =
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "Point",
                    "coordinates": [125.6, 10.1]
                },
                "properties": {
                    "name": "Dinagat Islands"
                }
            }
            """.trimIndent()

        compareJson(expectedJson, geoJsonString)
    }

    @Test
    fun linestring_feature_toJson() {
        val properties = JsonObject(
            content = mapOf(
                "name" to JsonPrimitive("Dinagat Islands")
            )
        )

        val points = listOf(
            Point(1.0, 1.0),
            Point(2.0, 2.0),
            Point(3.0, 3.0)
        )

        val lineString = LineString(points)

        val geo = Feature(lineString, properties)

        val actualFeature = Feature.fromJson(geo.toJson())
        val expectedFeature = Feature.fromJson(
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [1.0, 1.0],
                        [2.0, 2.0],
                        [3.0, 3.0]
                    ]
                },
                "properties": {
                    "name": "Dinagat Islands"
                }
            }
            """.trimIndent()
        )

        assertEquals(expectedFeature, actualFeature)
    }

    @Test
    fun testNullProperties() {
        val points = listOf(
            Point(0.1, 2.3),
            Point(4.5, 6.7)
        )

        val line = LineString(points)
        val feature = Feature(line)
        val jsonString = feature.toJson()
        assertFalse(jsonString.contains("\"properties\":{}"))

        // Feature (empty Properties) -> Json (null Properties) -> Equavalent Feature
        val featureFromJson: Feature = Feature.fromJson(jsonString)
        assertEquals(featureFromJson, feature)
    }

    @Test
    fun testNonNullProperties() {
        val points = listOf(
            Point(0.1, 2.3),
            Point(4.5, 6.7)
        )

        val line = LineString(points)
        val properties = JsonObject(
            content = mapOf(
                "key" to JsonPrimitive("value")
            )
        )

        val feature = Feature(line, properties)
        val jsonString = feature.toJson()
        assertTrue(jsonString.contains("\"properties\":{\"key\":\"value\"}"))

        // Feature (non-empty Properties) -> Json (non-empty Properties) -> Equavalent Feature
        assertEquals(Feature.fromJson(jsonString), feature)
    }

    @Test
    fun testNullPropertiesJson() {
        val jsonString =
            """
            {
                "type": "Feature",
                "bbox": [1.0, 2.0, 3.0, 4.0],
                "geometry": {
                    "type": "LineString",
                    "coordinates": [
                        [1.0, 2.0],
                        [2.0, 3.0]
                    ]
                }
            }
            """.trimIndent()

        val actualFeature = Feature.fromJson(Feature.fromJson(jsonString).toJson())
        val expectedFeature = Feature.fromJson(jsonString)
        assertEquals(expectedFeature, actualFeature)
    }

    @Test
    fun pointFeature_fromJson_toJson() {
        val jsonString =
                        """
                        {
                            "id": "id0",
                            "bbox": [-120.0, -60.0, 120.0, 60.0],
                            "geometry": {
                                "bbox": [-110.0, -50.0, 110.0, 50.0],
                                "coordinates": [100.0, 0.0],
                                "type": "Point"
                            },
                            "type": "Feature",
                            "properties": {
                                "prop0": "value0",
                                "prop1": "value1"
                            }
                        }
                        """.trimIndent()

        val actualFeature = Feature.fromJson(Feature.fromJson(jsonString).toJson())
        val expectedFeature = Feature.fromJson(jsonString)
        assertEquals(expectedFeature, actualFeature)
    }
}
