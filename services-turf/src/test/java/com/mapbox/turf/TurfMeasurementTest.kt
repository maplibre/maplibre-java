package com.mapbox.turf

import com.google.gson.JsonObject
import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.BoundingBox.Companion.fromPoints
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryCollection.Companion.fromGeometries
import com.mapbox.geojson.GeometryCollection.Companion.fromGeometry
import com.mapbox.geojson.LineString
import com.mapbox.geojson.LineString.Companion.fromLngLats
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.turf.TurfMeasurement.Companion.along
import com.mapbox.turf.TurfMeasurement.Companion.area
import com.mapbox.turf.TurfMeasurement.Companion.bbox
import com.mapbox.turf.TurfMeasurement.Companion.bboxPolygon
import com.mapbox.turf.TurfMeasurement.Companion.bearing
import com.mapbox.turf.TurfMeasurement.Companion.center
import com.mapbox.turf.TurfMeasurement.Companion.destination
import com.mapbox.turf.TurfMeasurement.Companion.distance
import com.mapbox.turf.TurfMeasurement.Companion.envelope
import com.mapbox.turf.TurfMeasurement.Companion.length
import com.mapbox.turf.TurfMeasurement.Companion.midpoint
import com.mapbox.turf.TurfMeasurement.Companion.square
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class TurfMeasurementTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    fun testBearing() {
        val pt1 = fromLngLat(-75.4, 39.4)
        val pt2 = fromLngLat(-75.534, 39.123)
        Assert.assertNotEquals(bearing(pt1, pt2), 0.0, DELTA)
    }

    @Test
    @Throws(TurfException::class)
    fun testDestination() {
        val pt1 = fromLngLat(-75.0, 39.0)
        val dist = 100.0
        val bear = 180.0
        Assert.assertNotNull(destination(pt1, dist, bear, TurfConstants.UNIT_KILOMETERS))
    }

    /*
   * Turf distance tests
   */
    @Test
    @Throws(TurfException::class)
    fun testDistance() {
        val pt1 = fromLngLat(-75.343, 39.984)
        val pt2 = fromLngLat(-75.534, 39.123)

        // Common cases
        Assert.assertEquals(
            60.37218405837491,
            distance(pt1, pt2, TurfConstants.UNIT_MILES),
            DELTA
        )
        Assert.assertEquals(
            52.461979624130436,
            distance(pt1, pt2, TurfConstants.UNIT_NAUTICAL_MILES), DELTA
        )
        Assert.assertEquals(
            97.15957803131901,
            distance(pt1, pt2, TurfConstants.UNIT_KILOMETERS),
            DELTA
        )
        Assert.assertEquals(
            0.015245501024842149,
            distance(pt1, pt2, TurfConstants.UNIT_RADIANS),
            DELTA
        )
        Assert.assertEquals(
            0.8735028650863799,
            distance(pt1, pt2, TurfConstants.UNIT_DEGREES),
            DELTA
        )

        // This also works
        Assert.assertEquals(
            97.15957803131901,
            distance(pt1, pt2, TurfConstants.UNIT_KILOMETERS),
            DELTA
        )

        // Default is kilometers
        Assert.assertEquals(97.15957803131901, distance(pt1, pt2), DELTA)

        // Bad units not possible
    }

    @Test
    @Throws(Exception::class)
    fun lineDistance_returnsZeroWhenRouteIsPoint() {
        val coords: MutableList<Point> = ArrayList()
        coords.add(fromLngLat(1.0, 1.0))
        val lineString = fromLngLats(coords)
        val distance = length(lineString, TurfConstants.UNIT_METERS)
        Assert.assertEquals(0.0, distance, DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineDistanceWithGeometries() {
        val route1 = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_ROUTE_ONE))
        val route2 = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_ROUTE_TWO))
        Assert.assertEquals(
            202, length(
                (route1.geometry() as LineString?)!!,
                TurfConstants.UNIT_MILES
            ).roundToInt()
        )
        Assert.assertEquals(
            741.7787396994203,
            length((route2.geometry() as LineString?)!!, TurfConstants.UNIT_KILOMETERS),
            DELTA
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineDistancePolygon() {
        val feature = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_POLYGON))
        Assert.assertEquals(
            5599, (1000 * length(
                (feature.geometry() as Polygon?)!!,
                TurfConstants.UNIT_KILOMETERS
            )).roundToLong()
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineDistanceMultiLineString() {
        val feature = Feature.fromJson(loadJsonFixture(LINE_DISTANCE_MULTILINESTRING))
        Assert.assertEquals(
            4705.0, (1000
                    * length(
                (feature.geometry() as MultiLineString?)!!,
                TurfConstants.UNIT_KILOMETERS
            )).roundToLong().toDouble(), DELTA
        )
    }

    /*
   * Turf midpoint tests
   */
    @Test
    @Throws(TurfException::class)
    fun testMidpointHorizontalEquator() {
        val pt1 = fromLngLat(0.0, 0.0)
        val pt2 = fromLngLat(10.0, 0.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun testMidpointVericalFromEquator() {
        val pt1 = fromLngLat(0.0, 0.0)
        val pt2 = fromLngLat(0.0, 10.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun testMidpointVericalToEquator() {
        val pt1 = fromLngLat(0.0, 10.0)
        val pt2 = fromLngLat(0.0, 0.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun testMidpointDiagonalBackOverEquator() {
        val pt1 = fromLngLat(-1.0, 10.0)
        val pt2 = fromLngLat(1.0, -1.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun testMidpointDiagonalForwardOverEquator() {
        val pt1 = fromLngLat(-5.0, -1.0)
        val pt2 = fromLngLat(5.0, 10.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun testMidpointLongDistance() {
        val pt1 = fromLngLat(22.5, 21.94304553343818)
        val pt2 = fromLngLat(92.10937499999999, 46.800059446787316)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    // Custom test to make sure conversion of Position to point works correctly
    @Test
    @Throws(TurfException::class)
    fun testMidpointPositionToPoint() {
        val pt1 = fromLngLat(0.0, 0.0)
        val pt2 = fromLngLat(10.0, 0.0)
        val mid = midpoint(pt1, pt2)
        Assert.assertEquals(
            distance(pt1, mid, TurfConstants.UNIT_MILES),
            distance(pt2, mid, TurfConstants.UNIT_MILES), DELTA
        )
    }

    @Test
    @Throws(Exception::class)
    fun turfAlong_returnsZeroWhenRouteIsPoint() {
        val coords: MutableList<Point> = ArrayList()
        coords.add(fromLngLat(1.0, 1.0))
        val lineString = fromLngLats(coords)
        val point = along(lineString, 0.0, TurfConstants.UNIT_METERS)
        Assert.assertEquals(1.0, point.latitude(), DELTA)
        Assert.assertEquals(1.0, point.longitude(), DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfAlong() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_ALONG_DC_LINE))
        val line = feature.geometry() as LineString?
        val pt1 = along(line!!, 1.0, "miles")
        val pt2 = along(line, 1.2, "miles")
        val pt3 = along(line, 1.4, "miles")
        val pt4 = along(line, 1.6, "miles")
        val pt5 = along(line, 1.8, "miles")
        val pt6 = along(line, 2.0, "miles")
        val pt7 = along(line, 100.0, "miles")
        val pt8 = along(line, 0.0, "miles")
        val fc = fromFeatures(
            arrayOf(
                Feature.fromGeometry(pt1),
                Feature.fromGeometry(pt2),
                Feature.fromGeometry(pt3),
                Feature.fromGeometry(pt4),
                Feature.fromGeometry(pt5),
                Feature.fromGeometry(pt6),
                Feature.fromGeometry(pt7),
                Feature.fromGeometry(pt8)
            )
        )
        for (f in fc.features()!!) {
            Assert.assertNotNull(f)
            Assert.assertEquals("Feature", f.type())
            Assert.assertEquals("Point", f.geometry()!!.type())
        }
        Assert.assertEquals(8, fc.features()!!.size.toLong())
        Assert.assertEquals(
            (fc.features()!![7].geometry() as Point?)!!.longitude(),
            pt8.longitude(),
            DELTA
        )
        Assert.assertEquals(
            (fc.features()!![7].geometry() as Point?)!!.latitude(),
            pt8.latitude(),
            DELTA
        )
    }

    /*
   * Turf bbox Test
   */
    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxFromPoint() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_BBOX_POINT))
        val bbox = bbox((feature.geometry() as Point?)!!)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(102.0, bbox[0], DELTA)
        Assert.assertEquals(0.5, bbox[1], DELTA)
        Assert.assertEquals(102.0, bbox[2], DELTA)
        Assert.assertEquals(0.5, bbox[3], DELTA)
    }

    @Test
    @Throws(TurfException::class, IOException::class)
    fun bboxFromLine() {
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_LINESTRING))
        val bbox = bbox(lineString)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(102.0, bbox[0], DELTA)
        Assert.assertEquals(-10.0, bbox[1], DELTA)
        Assert.assertEquals(130.0, bbox[2], DELTA)
        Assert.assertEquals(4.0, bbox[3], DELTA)
    }

    @Test
    @Throws(TurfException::class, IOException::class)
    fun bboxFromPolygon() {
        val feature = Feature.fromJson(loadJsonFixture(TURF_BBOX_POLYGON))
        val bbox = bbox((feature.geometry() as Polygon?)!!)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(100.0, bbox[0], DELTA)
        Assert.assertEquals(0.0, bbox[1], DELTA)
        Assert.assertEquals(101.0, bbox[2], DELTA)
        Assert.assertEquals(1.0, bbox[3], DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxFromMultiLineString() {
        val multiLineString = MultiLineString.fromJson(loadJsonFixture(TURF_BBOX_MULTILINESTRING))
        val bbox = bbox(multiLineString)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(100.0, bbox[0], DELTA)
        Assert.assertEquals(0.0, bbox[1], DELTA)
        Assert.assertEquals(103.0, bbox[2], DELTA)
        Assert.assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxFromMultiPolygon() {
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        val bbox = bbox(multiPolygon)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(100.0, bbox[0], DELTA)
        Assert.assertEquals(0.0, bbox[1], DELTA)
        Assert.assertEquals(103.0, bbox[2], DELTA)
        Assert.assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxFromGeometry() {
        val geometry: Geometry = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        val bbox = bbox(geometry)
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(100.0, bbox[0], DELTA)
        Assert.assertEquals(0.0, bbox[1], DELTA)
        Assert.assertEquals(103.0, bbox[2], DELTA)
        Assert.assertEquals(3.0, bbox[3], DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxFromGeometryCollection() {
        // Check that geometry collection and direct bbox are equal
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON))
        Assert.assertArrayEquals(
            bbox(multiPolygon),
            bbox(fromGeometry(multiPolygon)),
            DELTA
        )

        // Check all geometry types
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(Feature.fromJson(loadJsonFixture(TURF_BBOX_POINT)).geometry()!!)
        geometries.add(MultiPoint.fromJson(loadJsonFixture(TURF_BBOX_MULTI_POINT)))
        geometries.add(LineString.fromJson(loadJsonFixture(TURF_BBOX_LINESTRING)))
        geometries.add(MultiLineString.fromJson(loadJsonFixture(TURF_BBOX_MULTILINESTRING)))
        geometries.add(Feature.fromJson(loadJsonFixture(TURF_BBOX_POLYGON)).geometry()!!)
        geometries.add(MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_MULTIPOLYGON)))
        geometries.add(fromGeometry(fromLngLat(-1.0, -1.0)))
        val bbox = bbox(fromGeometries(geometries))
        Assert.assertEquals(4, bbox.size.toLong())
        Assert.assertEquals(-1.0, bbox[0], DELTA)
        Assert.assertEquals(-10.0, bbox[1], DELTA)
        Assert.assertEquals(130.0, bbox[2], DELTA)
        Assert.assertEquals(4.0, bbox[3], DELTA)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxPolygonFromLineString() {
        // Create a LineString
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_LINESTRING))

        // Use the LineString object to calculate its BoundingBox area
        val bbox = bbox(lineString)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = fromPoints(
            fromLngLat(bbox[0], bbox[1]), fromLngLat(
                bbox[2], bbox[3]
            )
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)
        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry() as Polygon?
        Assert.assertNotNull(polygonRepresentingBoundingBox)
        Assert.assertEquals(0, polygonRepresentingBoundingBox!!.inner().size.toLong())
        Assert.assertEquals(5, polygonRepresentingBoundingBox.coordinates()[0].size.toLong())
        Assert.assertEquals(
            fromLngLat(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][0]
        )
        Assert.assertEquals(
            fromLngLat(130.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][1]
        )
        Assert.assertEquals(
            fromLngLat(130.0, 4.0),
            polygonRepresentingBoundingBox.coordinates()[0][2]
        )
        Assert.assertEquals(
            fromLngLat(102.0, 4.0),
            polygonRepresentingBoundingBox.coordinates()[0][3]
        )
        Assert.assertEquals(
            fromLngLat(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][4]
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxPolygonFromLineStringWithId() {
        // Create a LineString
        val lineString = LineString.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_LINESTRING))

        // Use the LineString object to calculate its BoundingBox area
        val bbox = bbox(lineString)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = fromPoints(
            fromLngLat(bbox[0], bbox[1]), fromLngLat(
                bbox[2], bbox[3]
            )
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox, null, "TEST_ID")
        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry() as Polygon?
        Assert.assertNotNull(polygonRepresentingBoundingBox)
        Assert.assertEquals(0, polygonRepresentingBoundingBox!!.inner().size.toLong())
        Assert.assertEquals(5, polygonRepresentingBoundingBox.coordinates()[0].size.toLong())
        Assert.assertEquals("TEST_ID", featureRepresentingBoundingBox.id())
        Assert.assertEquals(
            fromLngLat(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][0]
        )
        Assert.assertEquals(
            fromLngLat(130.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][1]
        )
        Assert.assertEquals(
            fromLngLat(130.0, 4.0),
            polygonRepresentingBoundingBox.coordinates()[0][2]
        )
        Assert.assertEquals(
            fromLngLat(102.0, 4.0),
            polygonRepresentingBoundingBox.coordinates()[0][3]
        )
        Assert.assertEquals(
            fromLngLat(102.0, -10.0),
            polygonRepresentingBoundingBox.coordinates()[0][4]
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxPolygonFromMultiPolygon() {
        // Create a MultiPolygon
        val multiPolygon = MultiPolygon.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_MULTIPOLYGON))

        // Use the MultiPolygon object to calculate its BoundingBox area
        val bbox = bbox(multiPolygon)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = fromPoints(
            fromLngLat(bbox[0], bbox[1]), fromLngLat(
                bbox[2], bbox[3]
            )
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)
        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry() as Polygon?
        Assert.assertNotNull(polygonRepresentingBoundingBox)
        Assert.assertEquals(0, polygonRepresentingBoundingBox!!.inner().size.toLong())
        Assert.assertEquals(5, polygonRepresentingBoundingBox.coordinates()[0].size.toLong())
        Assert.assertEquals(
            fromLngLat(100.0, 0.0),
            polygonRepresentingBoundingBox.coordinates()[0][4]
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun bboxPolygonFromMultiPoint() {
        // Create a MultiPoint
        val multiPoint = MultiPoint.fromJson(loadJsonFixture(TURF_BBOX_POLYGON_MULTI_POINT))

        // Use the MultiPoint object to calculate its BoundingBox area
        val bbox = bbox(multiPoint)

        // Use the BoundingBox coordinates to create an actual BoundingBox object
        val boundingBox = fromPoints(
            fromLngLat(bbox[0], bbox[1]), fromLngLat(
                bbox[2], bbox[3]
            )
        )

        // Use the BoundingBox object in the TurfMeasurement.bboxPolygon() method.
        val featureRepresentingBoundingBox = bboxPolygon(boundingBox)
        val polygonRepresentingBoundingBox = featureRepresentingBoundingBox.geometry() as Polygon?
        Assert.assertNotNull(polygonRepresentingBoundingBox)
        Assert.assertEquals(0, polygonRepresentingBoundingBox!!.inner().size.toLong())
        Assert.assertEquals(5, polygonRepresentingBoundingBox.coordinates()[0].size.toLong())
    }

    @Test
    @Throws(IOException::class)
    fun envelope() {
        val featureCollection = FeatureCollection.fromJson(
            loadJsonFixture(
                TURF_ENVELOPE_FEATURE_COLLECTION
            )
        )
        val polygon = envelope(featureCollection)
        val expectedPoints: MutableList<Point> = ArrayList()
        expectedPoints.add(fromLngLat(20.0, -10.0))
        expectedPoints.add(fromLngLat(130.0, -10.0))
        expectedPoints.add(fromLngLat(130.0, 4.0))
        expectedPoints.add(fromLngLat(20.0, 4.0))
        expectedPoints.add(fromLngLat(20.0, -10.0))
        val polygonPoints: List<List<Point>> = object : ArrayList<List<Point>>() {
            init {
                add(expectedPoints)
            }
        }
        val expected = fromLngLats(polygonPoints)
        Assert.assertEquals("Polygon should match.", expected, polygon)
    }

    @Test
    fun square() {
        val bbox1 = fromLngLats(0.0, 0.0, 5.0, 10.0)
        val bbox2 = fromLngLats(0.0, 0.0, 10.0, 5.0)
        val sq1 = square(bbox1)
        val sq2 = square(bbox2)
        Assert.assertEquals(fromLngLats(-2.5, 0.0, 7.5, 10.0), sq1)
        Assert.assertEquals(fromLngLats(0.0, -2.5, 10.0, 7.5), sq2)
    }

    @Test
    fun areaPolygon() {
        val expected = loadJsonFixture(TURF_AREA_POLYGON_RESULT).toDouble()
        Assert.assertEquals(
            expected, area(
                Feature.fromJson(
                    loadJsonFixture(
                        TURF_AREA_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaMultiPolygon() {
        val expected = loadJsonFixture(TURF_AREA_MULTIPOLYGON_RESULT).toDouble()
        Assert.assertEquals(
            expected, area(
                Feature.fromJson(
                    loadJsonFixture(
                        TURF_AREA_MULTIPOLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaGeometry() {
        val expected = loadJsonFixture(TURF_AREA_GEOM_POLYGON_RESULT).toDouble()
        Assert.assertEquals(
            expected, area(
                Polygon.fromJson(
                    loadJsonFixture(
                        TURF_AREA_GEOM_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun areaFeatureCollection() {
        val expected = loadJsonFixture(TURF_AREA_FEATURECOLLECTION_POLYGON_RESULT).toDouble()
        Assert.assertEquals(
            expected, area(
                FeatureCollection.fromJson(
                    loadJsonFixture(
                        TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON
                    )
                )
            ), 1.0
        )
    }

    @Test
    fun centerFeature() {
        val expectedFeature = Feature.fromGeometry(fromLngLat(133.5, -27.0))
        val inputFeature = Feature.fromJson(loadJsonFixture(TURF_AREA_POLYGON_GEOJSON))
        Assert.assertEquals(expectedFeature, center(inputFeature, null, null))
    }

    @Test
    fun centerFeatureWithProperties() {
        val properties = JsonObject()
        properties.addProperty("key", "value")
        val inputFeature = Feature.fromJson(loadJsonFixture(TURF_AREA_POLYGON_GEOJSON))
        val returnedCenterFeature = center(inputFeature, properties, null)
        val returnedPoint = returnedCenterFeature.geometry() as Point?
        if (returnedPoint != null) {
            Assert.assertEquals(133.5, returnedPoint.longitude(), 0.0)
            Assert.assertEquals(-27.0, returnedPoint.latitude(), 0.0)
            Assert.assertTrue(
                returnedCenterFeature.properties().toString().contains("{\"key\":\"value\"}")
            )
        }
    }

    @Test
    fun centerFeatureWithId() {
        val testIdString = "testId"
        val inputFeature = Feature.fromJson(loadJsonFixture(TURF_AREA_POLYGON_GEOJSON))
        val returnedCenterFeature = center(inputFeature, null, testIdString)
        val returnedPoint = returnedCenterFeature.geometry() as Point?
        if (returnedPoint != null) {
            Assert.assertEquals(133.5, returnedPoint.longitude(), 0.0)
            Assert.assertEquals(-27.0, returnedPoint.latitude(), 0.0)
            if (returnedCenterFeature.id() != null) {
                Assert.assertEquals(returnedCenterFeature.id(), testIdString)
            }
        }
    }

    @Test
    fun centerFeatureCollection() {
        val inputFeatureCollection = FeatureCollection.fromJson(
            loadJsonFixture(
                TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON
            )
        )
        val returnedCenterFeature = center(inputFeatureCollection, null, null)
        val returnedPoint = returnedCenterFeature.geometry() as Point?
        if (returnedPoint != null) {
            Assert.assertEquals(4.1748046875, returnedPoint.longitude(), DELTA)
            Assert.assertEquals(
                47.214224817196836,
                returnedPoint.latitude(),
                DELTA
            )
        }
    }

    companion object {
        private const val LINE_DISTANCE_ROUTE_ONE = "turf-line-distance/route1.geojson"
        private const val LINE_DISTANCE_ROUTE_TWO = "turf-line-distance/route2.geojson"
        private const val LINE_DISTANCE_POLYGON = "turf-line-distance/polygon.geojson"
        private const val TURF_ALONG_DC_LINE = "turf-along/dc-line.geojson"
        private const val TURF_BBOX_POINT = "turf-bbox/point.geojson"
        private const val TURF_BBOX_MULTI_POINT = "turf-bbox/multipoint.geojson"
        private const val TURF_BBOX_LINESTRING = "turf-bbox/linestring.geojson"
        private const val TURF_BBOX_POLYGON = "turf-bbox/polygon.geojson"
        private const val TURF_BBOX_MULTILINESTRING = "turf-bbox/multilinestring.geojson"
        private const val TURF_BBOX_MULTIPOLYGON = "turf-bbox/multipolygon.geojson"
        private const val TURF_BBOX_POLYGON_LINESTRING = "turf-bbox-polygon/linestring.geojson"
        private const val TURF_BBOX_POLYGON_MULTIPOLYGON = "turf-bbox-polygon/multipolygon.geojson"
        private const val TURF_BBOX_POLYGON_MULTI_POINT = "turf-bbox-polygon/multipoint.geojson"
        private const val TURF_ENVELOPE_FEATURE_COLLECTION =
            "turf-envelope/feature-collection.geojson"
        private const val LINE_DISTANCE_MULTILINESTRING =
            "turf-line-distance/multilinestring.geojson"
        private const val TURF_AREA_POLYGON_GEOJSON = "turf-area/polygon.geojson"
        private const val TURF_AREA_POLYGON_RESULT = "turf-area/polygon.json"
        private const val TURF_AREA_MULTIPOLYGON_GEOJSON = "turf-area/multi-polygon.geojson"
        private const val TURF_AREA_MULTIPOLYGON_RESULT = "turf-area/multi-polygon.json"
        private const val TURF_AREA_GEOM_POLYGON_GEOJSON = "turf-area/geometry-polygon.geojson"
        private const val TURF_AREA_GEOM_POLYGON_RESULT = "turf-area/geometry-polygon.json"
        private const val TURF_AREA_FEATURECOLLECTION_POLYGON_GEOJSON =
            "turf-area/featurecollection-polygon.geojson"
        private const val TURF_AREA_FEATURECOLLECTION_POLYGON_RESULT =
            "turf-area/featurecollection-polygon.json"
    }
}