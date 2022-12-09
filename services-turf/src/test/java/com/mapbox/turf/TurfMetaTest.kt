package com.mapbox.turf

import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection.Companion.fromFeature
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryCollection.Companion.fromGeometries
import com.mapbox.geojson.LineString
import com.mapbox.geojson.LineString.Companion.fromLngLats
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon
import com.mapbox.turf.TurfMeta.coordAll
import com.mapbox.turf.TurfMeta.getCoord
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*

class TurfMetaTest : TestUtils() {
    @Rule
    @JvmField
    var thrown : ExpectedException = ExpectedException.none()
    @Test
    @Throws(TurfException::class)
    fun coordAllPoint() {
        val jsonPoint = "{type: 'Point', coordinates: [0, 0]}"
        val pointGeometry = Point.fromJson(jsonPoint)
        val resultList = coordAll(pointGeometry)
        Assert.assertEquals(resultList.size.toDouble(), 1.0, DELTA)
        Assert.assertEquals(resultList[0], fromLngLat(0.0, 0.0))
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllLineString() {
        val jsonLineString = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val lineStringGeometry = LineString.fromJson(jsonLineString)
        val resultList = coordAll(lineStringGeometry)
        Assert.assertEquals(resultList.size.toDouble(), 2.0, DELTA)
        Assert.assertEquals(resultList[0], fromLngLat(0.0, 0.0))
        Assert.assertEquals(resultList[1], fromLngLat(1.0, 1.0))
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllPolygon() {
        val polygonString = "{type: 'Polygon', coordinates: [[[0, 0], [1, 1], [0, 1], [0, 0]]]}"
        val polygonGeometry = Polygon.fromJson(polygonString)
        val resultList = coordAll(polygonGeometry, false)
        Assert.assertEquals(resultList.size.toDouble(), 4.0, DELTA)
        Assert.assertEquals(resultList[0], fromLngLat(0.0, 0.0))
        Assert.assertEquals(resultList[1], fromLngLat(1.0, 1.0))
        Assert.assertEquals(resultList[2], fromLngLat(0.0, 1.0))
        Assert.assertEquals(resultList[3], fromLngLat(0.0, 0.0))
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllPolygonExcludeWrapCoord() {
        val polygonString = "{type: 'Polygon', coordinates: [[[0, 0], [1, 1], [0, 1], [0, 0]]]}"
        val polygonGeometry = Polygon.fromJson(polygonString)
        val resultList = coordAll(polygonGeometry, true)
        Assert.assertEquals(resultList.size.toDouble(), 3.0, DELTA)
        Assert.assertEquals(resultList[0], fromLngLat(0.0, 0.0))
        Assert.assertEquals(resultList[1], fromLngLat(1.0, 1.0))
        Assert.assertEquals(resultList[2], fromLngLat(0.0, 1.0))
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllMultiPolygon() {
        val multipolygonString =
            "{type: 'MultiPolygon', coordinates: [[[[0, 0], [1, 1], [0, 1], [0, 0]]]]}"
        val multiPolygonGeometry = MultiPolygon.fromJson(multipolygonString)
        val resultList = coordAll(multiPolygonGeometry, false)
        Assert.assertEquals(resultList.size.toDouble(), 4.0, DELTA)
        Assert.assertEquals(resultList[0], fromLngLat(0.0, 0.0))
        Assert.assertEquals(resultList[1], fromLngLat(1.0, 1.0))
        Assert.assertEquals(resultList[2], fromLngLat(0.0, 1.0))
        Assert.assertEquals(resultList[3], fromLngLat(0.0, 0.0))
    }

    @Test
    fun testInvariantGetCoord() {
        val jsonFeature = "{type: 'Feature', geometry: {type: 'Point', coordinates: [1, 2]}}"
        Assert.assertEquals(
            getCoord(Feature.fromJson(jsonFeature)),
            fromLngLat(1.0, 2.0)
        )
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllFeatureCollection() {
        val multipolygonJson =
            "{type: 'MultiPolygon', coordinates: [[[[0, 0], [1, 1], [0, 1], [0, 0]]]]}"
        val lineStringJson = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val featureCollection = fromFeatures(
            arrayOf(
                Feature.fromGeometry(MultiPolygon.fromJson(multipolygonJson)),
                Feature.fromGeometry(LineString.fromJson(lineStringJson))
            )
        )
        Assert.assertNotNull(featureCollection)
        Assert.assertEquals(5, coordAll(featureCollection, true).size.toLong())
        Assert.assertEquals(
            0.0,
            coordAll(featureCollection, true)[0].latitude(),
            DELTA
        )
        Assert.assertEquals(
            0.0,
            coordAll(featureCollection, true)[0].longitude(),
            DELTA
        )
        Assert.assertEquals(
            1.0,
            coordAll(featureCollection, true)[4].latitude(),
            DELTA
        )
        Assert.assertEquals(
            1.0,
            coordAll(featureCollection, true)[4].longitude(),
            DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllSingleFeature() {
        val lineStringJson = "{type: 'LineString', coordinates: [[0, 0], [1, 1]]}"
        val featureCollection = fromFeature(
            Feature.fromGeometry(LineString.fromJson(lineStringJson))
        )
        Assert.assertNotNull(featureCollection)
        Assert.assertEquals(2, coordAll(featureCollection, true).size.toLong())
        Assert.assertEquals(
            0.0,
            coordAll(featureCollection, true)[0].latitude(),
            DELTA
        )
        Assert.assertEquals(
            0.0,
            coordAll(featureCollection, true)[0].longitude(),
            DELTA
        )
        Assert.assertEquals(
            1.0,
            coordAll(featureCollection, true)[1].latitude(),
            DELTA
        )
        Assert.assertEquals(
            1.0,
            coordAll(featureCollection, true)[1].longitude(),
            DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun coordAllGeometryCollection() {
        val points: MutableList<Point> = ArrayList()
        points.add(fromLngLat(1.0, 2.0))
        points.add(fromLngLat(2.0, 3.0))
        val lineString = fromLngLats(points)
        val geometries: MutableList<Geometry> = ArrayList()
        geometries.add(points[0])
        geometries.add(lineString)
        val bbox = fromLngLats(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = fromGeometries(geometries, bbox)
        val featureCollection = fromFeature(
            Feature.fromGeometry(geometryCollection)
        )
        Assert.assertNotNull(featureCollection)
        Assert.assertNotNull(coordAll(featureCollection, true))
        Assert.assertEquals(3, coordAll(featureCollection, true).size.toLong())
        Assert.assertEquals(
            1.0,
            coordAll(featureCollection, true)[0].longitude(),
            DELTA
        )
        Assert.assertEquals(
            2.0,
            coordAll(featureCollection, true)[0].latitude(),
            DELTA
        )
    }

    @Test
    @Throws(TurfException::class)
    fun wrongFeatureGeometryForGetCoordThrowsException() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("A Feature with a Point geometry is required."))
        getCoord(
            Feature.fromGeometry(
                fromLngLats(
                    listOf(
                        fromLngLat(0.0, 9.0),
                        fromLngLat(0.0, 10.0)
                    )
                )
            )
        )
    }
}