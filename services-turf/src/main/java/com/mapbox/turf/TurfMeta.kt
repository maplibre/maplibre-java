package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon

/**
 * Class contains methods that are useful for getting all coordinates from a specific GeoJson
 * geometry.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 2.0.0
 */
object TurfMeta {
    /**
     * Get all coordinates from a [Point] object, returning a `List` of Point objects.
     * If you have a geometry collection, you need to break it down to individual geometry objects
     * before using [.coordAll].
     *
     * @param point any [Point] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(point: Point): List<Point> {
        return coordAll(ArrayList(), point)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords the `List` of [Point]s.
     * @param point  any [Point] object
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(coords: MutableList<Point>, point: Point): List<Point> {
        coords.add(point)
        return coords
    }

    /**
     * Get all coordinates from a [MultiPoint] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param multiPoint any [MultiPoint] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(multiPoint: MultiPoint): List<Point> {
        return coordAll(ArrayList(), multiPoint)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords     the `List` of [Point]s.
     * @param multiPoint any [MultiPoint] object
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(coords: MutableList<Point>, multiPoint: MultiPoint): List<Point> {
        coords.addAll(multiPoint.coordinates())
        return coords
    }

    /**
     * Get all coordinates from a [LineString] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param lineString any [LineString] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(lineString: LineString): List<Point> {
        return coordAll(ArrayList(), lineString)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords     the `List` of [Point]s.
     * @param lineString any [LineString] object
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(coords: MutableList<Point>, lineString: LineString): List<Point> {
        coords.addAll(lineString.coordinates())
        return coords
    }

    /**
     * Get all coordinates from a [Polygon] object, returning a `List` of Point objects.
     * If you have a geometry collection, you need to break it down to individual geometry objects
     * before using [.coordAll].
     *
     * @param polygon          any [Polygon] object
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(polygon: Polygon, excludeWrapCoord: Boolean): List<Point> {
        return coordAll(ArrayList(), polygon, excludeWrapCoord)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords           the `List` of [Point]s.
     * @param polygon          any [Polygon] object
     * @param excludeWrapCoord whether or not to include the final
     * coordinate of LinearRings that
     * wraps the ring in its iteration
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(
        coords: MutableList<Point>,
        polygon: Polygon,
        excludeWrapCoord: Boolean
    ): List<Point> {
        val wrapShrink = if (excludeWrapCoord) 1 else 0
        for (i in polygon.coordinates().indices) {
            for (j in 0 until polygon.coordinates()[i].size - wrapShrink) {
                coords.add(polygon.coordinates()[i][j])
            }
        }
        return coords
    }

    /**
     * Get all coordinates from a [MultiLineString] object, returning
     * a `List` of Point objects. If you have a geometry collection, you
     * need to break it down to individual geometry objects before using
     * [.coordAll].
     *
     * @param multiLineString any [MultiLineString] object
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(multiLineString: MultiLineString): List<Point> {
        return coordAll(ArrayList(), multiLineString)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords          the `List` of [Point]s.
     * @param multiLineString any [MultiLineString] object
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(
        coords: MutableList<Point>,
        multiLineString: MultiLineString
    ): List<Point> {
        for (i in multiLineString.coordinates().indices) {
            coords.addAll(multiLineString.coordinates()[i])
        }
        return coords
    }

    /**
     * Get all coordinates from a [MultiPolygon] object, returning a `List` of Point
     * objects. If you have a geometry collection, you need to break it down to individual geometry
     * objects before using [.coordAll].
     *
     * @param multiPolygon     any [MultiPolygon] object
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used to handle [Polygon] and
     * [MultiPolygon] geometries.
     * @return a `List` made up of [Point]s
     * @since 2.0.0
     */
    @JvmStatic
    fun coordAll(
        multiPolygon: MultiPolygon,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return coordAll(ArrayList(), multiPolygon, excludeWrapCoord)
    }

    /**
     * Private helper method to go with [TurfMeta.coordAll].
     *
     * @param coords           the `List` of [Point]s.
     * @param multiPolygon     any [MultiPolygon] object
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used to handle [Polygon] and
     * [MultiPolygon] geometries.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    private fun coordAll(
        coords: MutableList<Point>,
        multiPolygon: MultiPolygon,
        excludeWrapCoord: Boolean
    ): List<Point> {
        val wrapShrink = if (excludeWrapCoord) 1 else 0
        for (i in multiPolygon.coordinates()!!.indices) {
            for (j in multiPolygon.coordinates()!![i].indices) {
                for (k in 0 until multiPolygon.coordinates()!![i][j].size - wrapShrink) {
                    coords.add(multiPolygon.coordinates()!![i][j][k])
                }
            }
        }
        return coords
    }

    /**
     * Get all coordinates from a [Feature] object, returning a `List` of [Point]
     * objects.
     *
     * @param feature          the [Feature] that you'd like to extract the Points from.
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if the [Feature]
     * passed through the method is a [Polygon] or [MultiPolygon]
     * geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    fun coordAll(
        feature: Feature,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return addCoordAll(ArrayList(), feature, excludeWrapCoord)
    }

    /**
     * Get all coordinates from a [FeatureCollection] object, returning a
     * `List` of [Point] objects.
     *
     * @param featureCollection the [FeatureCollection] that you'd like
     * to extract the Points from.
     * @param excludeWrapCoord  whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if a [Feature] in the
     * [FeatureCollection] that's passed through this method, is a
     * [Polygon] or [MultiPolygon] geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    @JvmStatic
    fun coordAll(
        featureCollection: FeatureCollection,
        excludeWrapCoord: Boolean
    ): List<Point> {
        val finalCoordsList: MutableList<Point> = ArrayList()
        for (singleFeature in featureCollection.features()!!) {
            addCoordAll(finalCoordsList, singleFeature, excludeWrapCoord)
        }
        return finalCoordsList
    }

    /**
     * Private helper method to be used with other methods in this class.
     *
     * @param pointList the `List` of [Point]s.
     * @param feature the [Feature] that you'd like
     * to extract the Points from.
     * @param excludeWrapCoord  whether or not to include the final
     * coordinate of LinearRings that wraps the ring
     * in its iteration. Used if a [Feature] in the
     * [FeatureCollection] that's passed through
     * this method, is a [Polygon] or [MultiPolygon]
     * geometry.
     * @return a `List` made up of [Point]s.
     * @since 4.8.0
     */
    private fun addCoordAll(
        pointList: MutableList<Point>, feature: Feature,
        excludeWrapCoord: Boolean
    ): List<Point> {
        return coordAllFromSingleGeometry(pointList, feature.geometry()!!, excludeWrapCoord)
    }

    /**
     * Get all coordinates from a [FeatureCollection] object, returning a
     * `List` of [Point] objects.
     *
     * @param pointList        the `List` of [Point]s.
     * @param geometry         the [Geometry] object to extract the [Point]s from
     * @param excludeWrapCoord whether or not to include the final coordinate of LinearRings that
     * wraps the ring in its iteration. Used if the [Feature]
     * passed through the method is a [Polygon] or [MultiPolygon]
     * geometry.
     * @return a `List` made up of [Point]s
     * @since 4.8.0
     */
    private fun coordAllFromSingleGeometry(
        pointList: MutableList<Point>,
        geometry: Geometry,
        excludeWrapCoord: Boolean
    ): List<Point> {
        when (geometry) {
            is Point -> {
                pointList.add(geometry)
            }
            is MultiPoint -> {
                pointList.addAll(geometry.coordinates())
            }
            is LineString -> {
                pointList.addAll(geometry.coordinates())
            }
            is MultiLineString -> {
                coordAll(pointList, geometry)
            }
            is Polygon -> {
                coordAll(pointList, geometry, excludeWrapCoord)
            }
            is MultiPolygon -> {
                coordAll(pointList, geometry, excludeWrapCoord)
            }
            is GeometryCollection -> {
                // recursive
                for (singleGeometry in geometry.geometries()) {
                    coordAllFromSingleGeometry(pointList, singleGeometry, excludeWrapCoord)
                }
            }
        }
        return pointList
    }

    /**
     * Unwrap a coordinate [Point] from a [Feature] with a [Point] geometry.
     *
     * @param obj any value
     * @return a coordinate
     * @see [Turf getCoord documentation](http://turfjs.org/docs/.getcoord)
     *
     * @since 3.2.0
     */
    @JvmStatic
    fun getCoord(obj: Feature): Point? {
        if (obj.geometry() is Point) {
            return obj.geometry() as Point?
        }
        throw TurfException("A Feature with a Point geometry is required.")
    }
}