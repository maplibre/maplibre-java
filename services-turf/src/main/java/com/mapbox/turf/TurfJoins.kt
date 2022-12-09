package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon

/**
 * Class contains methods that can determine if points lie within a polygon or not.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.3.0
 */
object TurfJoins {
    /**
     * Takes a [Point] and a [Polygon] and determines if the point resides inside the
     * polygon. The polygon can be convex or concave. The function accounts for holes.
     *
     * @param point   which you'd like to check if inside the polygon
     * @param polygon which you'd like to check if the points inside
     * @return true if the Point is inside the Polygon; false if the Point is not inside the Polygon
     * @see [Turf Inside documentation](http://turfjs.org/docs/.inside)
     *
     * @since 1.3.0
     */
    @kotlin.jvm.JvmStatic
    fun inside(point: Point?, polygon: Polygon?): Boolean {
        // This API needs to get better
        val coordinates = polygon!!.coordinates()
        val multiCoordinates: MutableList<List<List<Point>>> = ArrayList()
        multiCoordinates.add(coordinates)
        return inside(point!!, MultiPolygon.fromLngLats(multiCoordinates))
    }

    /**
     * Takes a [Point] and a [MultiPolygon] and determines if the point resides inside
     * the polygon. The polygon can be convex or concave. The function accounts for holes.
     *
     * @param point        which you'd like to check if inside the polygon
     * @param multiPolygon which you'd like to check if the points inside
     * @return true if the Point is inside the MultiPolygon; false if the Point is not inside the
     * MultiPolygon
     * @see [Turf Inside documentation](http://turfjs.org/docs/.inside)
     *
     * @since 1.3.0
     */
    @JvmStatic
    fun inside(point: Point, multiPolygon: MultiPolygon): Boolean {
        val polys = multiPolygon.coordinates()
        var insidePoly = false
        var i = 0
        while (i < polys!!.size && !insidePoly) {

            // check if it is in the outer ring first
            if (inRing(point, polys[i][0])) {
                var inHole = false
                var temp = 1
                // check for the point in any of the holes
                while (temp < polys[i].size && !inHole) {
                    if (inRing(point, polys[i][temp])) {
                        inHole = true
                    }
                    temp++
                }
                if (!inHole) {
                    insidePoly = true
                }
            }
            i++
        }
        return insidePoly
    }

    /**
     * Takes a [FeatureCollection] of [Point] and a [FeatureCollection] of
     * [Polygon] and returns the points that fall within the polygons.
     *
     * @param points   input points.
     * @param polygons input polygons.
     * @return points that land within at least one polygon.
     * @since 1.3.0
     */
    @JvmStatic
    fun pointsWithinPolygon(
        points: FeatureCollection,
        polygons: FeatureCollection
    ): FeatureCollection {
        val features = ArrayList<Feature>()
        for (i in polygons.features()!!.indices) {
            for (j in points.features()!!.indices) {
                val point = points.features()!![j].geometry() as Point?
                val isInside = inside(point, polygons.features()!![i].geometry() as Polygon?)
                if (isInside) {
                    features.add(Feature.fromGeometry(point))
                }
            }
        }
        return fromFeatures(features)
    }

    // pt is [x,y] and ring is [[x,y], [x,y],..]
    private fun inRing(pt: Point, ring: List<Point>): Boolean {
        var isInside = false
        var i = 0
        var j = ring.size - 1
        while (i < ring.size) {
            val xi = ring[i].longitude()
            val yi = ring[i].latitude()
            val xj = ring[j].longitude()
            val yj = ring[j].latitude()
            val intersect = (yi > pt.latitude() != yj > pt.latitude()
                    && pt.longitude() < (xj - xi) * (pt.latitude() - yi) / (yj - yi) + xi)
            if (intersect) {
                isInside = !isInside
            }
            j = i++
        }
        return isInside
    }
}