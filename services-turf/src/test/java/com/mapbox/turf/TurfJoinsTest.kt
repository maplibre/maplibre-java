package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.turf.TurfJoins.inside
import com.mapbox.turf.TurfJoins.pointsWithinPolygon
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.util.*

class TurfJoinsTest : TestUtils() {
    @Test
    @Throws(TurfException::class)
    fun testFeatureCollection() {
        // Test for a simple polygon
        var pointList = ArrayList<Point>()
        pointList.add(fromLngLat(0.0, 0.0))
        pointList.add(fromLngLat(0.0, 100.0))
        pointList.add(fromLngLat(100.0, 100.0))
        pointList.add(fromLngLat(100.0, 0.0))
        pointList.add(fromLngLat(0.0, 0.0))
        var coordinates: MutableList<List<Point>> = ArrayList()
        coordinates.add(pointList)
        val poly: Polygon = Polygon.fromLngLats(coordinates)
        var ptIn = fromLngLat(50.0, 50.0)
        var ptOut = fromLngLat(140.0, 150.0)
        Assert.assertTrue(inside(ptIn, poly))
        Assert.assertFalse(inside(ptOut, poly))

        // Test for a concave polygon
        pointList = ArrayList()
        pointList.add(fromLngLat(0.0, 0.0))
        pointList.add(fromLngLat(50.0, 50.0))
        pointList.add(fromLngLat(0.0, 100.0))
        pointList.add(fromLngLat(100.0, 100.0))
        pointList.add(fromLngLat(100.0, 0.0))
        pointList.add(fromLngLat(0.0, 0.0))
        coordinates = ArrayList()
        coordinates.add(pointList)
        val concavePoly: Polygon = Polygon.fromLngLats(coordinates)
        ptIn = fromLngLat(75.0, 75.0)
        ptOut = fromLngLat(25.0, 50.0)
        Assert.assertTrue(inside(ptIn, concavePoly))
        Assert.assertFalse(inside(ptOut, concavePoly))
    }

    @Test
    @Throws(TurfException::class, IOException::class)
    fun testPolyWithHole() {
        val ptInHole = fromLngLat(-86.69208526611328, 36.20373274711739)
        val ptInPoly = fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = fromLngLat(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))
        Assert.assertFalse(inside(ptInHole, polyHole.geometry() as Polygon?))
        Assert.assertTrue(inside(ptInPoly, polyHole.geometry() as Polygon?))
        Assert.assertFalse(inside(ptOutsidePoly, polyHole.geometry() as Polygon?))
    }

    @Test
    @Throws(TurfException::class, IOException::class)
    fun testMultipolygonWithHole() {
        val ptInHole = fromLngLat(-86.69208526611328, 36.20373274711739)
        val ptInPoly = fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptInPoly2 = fromLngLat(-86.75079345703125, 36.18527313913089)
        val ptOutsidePoly = fromLngLat(-86.75302505493164, 36.23015046460186)
        val multiPolyHole = Feature.fromJson(loadJsonFixture(MULTIPOLY_WITH_HOLE_FIXTURE))
        Assert.assertFalse(inside(ptInHole, (multiPolyHole.geometry() as MultiPolygon?)!!))
        Assert.assertTrue(inside(ptInPoly, (multiPolyHole.geometry() as MultiPolygon?)!!))
        Assert.assertTrue(inside(ptInPoly2, (multiPolyHole.geometry() as MultiPolygon?)!!))
        Assert.assertFalse(inside(ptOutsidePoly, (multiPolyHole.geometry() as MultiPolygon?)!!))
    }

    /*
   * Custom test
   */
    @Test
    @Throws(IOException::class, TurfException::class)
    fun testInputPositions() {
        val ptInPoly = fromLngLat(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = fromLngLat(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(loadJsonFixture(POLY_WITH_HOLE_FIXTURE))
        val polygon = polyHole.geometry() as Polygon?
        Assert.assertTrue(inside(ptInPoly, polygon))
        Assert.assertFalse(inside(ptOutsidePoly, polygon))
    }

    @Test
    @Throws(TurfException::class)
    fun testWithin() {
        // Test with a single point
        val pointList = ArrayList<Point>()
        pointList.add(fromLngLat(0.0, 0.0))
        pointList.add(fromLngLat(0.0, 100.0))
        pointList.add(fromLngLat(100.0, 100.0))
        pointList.add(fromLngLat(100.0, 0.0))
        pointList.add(fromLngLat(0.0, 0.0))
        val coordinates = ArrayList<List<Point>>()
        coordinates.add(pointList)
        val poly = fromLngLats(coordinates)
        val pt = fromLngLat(50.0, 50.0)
        val features1 = ArrayList<Feature>()
        features1.add(Feature.fromGeometry(poly))
        var polyFeatureCollection = fromFeatures(features1)
        val features2 = ArrayList<Feature>()
        features2.add(Feature.fromGeometry(pt))
        var ptFeatureCollection = fromFeatures(features2)
        var counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        Assert.assertNotNull(counted)
        Assert.assertEquals(counted.features()!!.size.toLong(), 1) // 1 point in 1 polygon

        // test with multiple points and multiple polygons
        val poly1 = fromLngLats(
            Arrays.asList(
                Arrays.asList(
                    fromLngLat(0.0, 0.0),
                    fromLngLat(10.0, 0.0),
                    fromLngLat(10.0, 10.0),
                    fromLngLat(0.0, 10.0),
                    fromLngLat(0.0, 0.0)
                )
            )
        )
        val poly2 = fromLngLats(
            Arrays.asList(
                Arrays.asList(
                    fromLngLat(10.0, 0.0),
                    fromLngLat(20.0, 10.0),
                    fromLngLat(20.0, 20.0),
                    fromLngLat(20.0, 0.0),
                    fromLngLat(10.0, 0.0)
                )
            )
        )
        polyFeatureCollection = fromFeatures(
            arrayOf(
                Feature.fromGeometry(poly1),
                Feature.fromGeometry(poly2)
            )
        )
        val pt1 = fromLngLat(1.0, 1.0)
        val pt2 = fromLngLat(1.0, 3.0)
        val pt3 = fromLngLat(14.0, 2.0)
        val pt4 = fromLngLat(13.0, 1.0)
        val pt5 = fromLngLat(19.0, 7.0)
        val pt6 = fromLngLat(100.0, 7.0)
        ptFeatureCollection = fromFeatures(
            arrayOf(
                Feature.fromGeometry(pt1), Feature.fromGeometry(pt2), Feature.fromGeometry(pt3),
                Feature.fromGeometry(pt4), Feature.fromGeometry(pt5), Feature.fromGeometry(pt6)
            )
        )
        counted = pointsWithinPolygon(ptFeatureCollection, polyFeatureCollection)
        Assert.assertNotNull(counted)
        Assert.assertEquals(
            counted.features()!!.size.toLong(),
            5
        ) // multiple points in multiple polygons
    }

    companion object {
        private const val POLY_WITH_HOLE_FIXTURE = "turf-inside/poly-with-hole.geojson"
        private const val MULTIPOLY_WITH_HOLE_FIXTURE = "turf-inside/multipoly-with-hole.geojson"
    }
}