package com.mapbox.turf

import androidx.annotation.FloatRange
import com.google.gson.JsonObject
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.BoundingBox.Companion.fromLngLats
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.GeometryCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.MultiPoint.Companion.fromLngLats
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.geojson.Polygon
import com.mapbox.geojson.Polygon.Companion.fromLngLats
import com.mapbox.turf.TurfConstants.TurfUnitCriteria
import java.util.*
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Class contains an assortment of methods used to calculate measurements such as bearing,
 * destination, midpoint, etc.
 *
 * @see [Turf documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
class TurfMeasurement private constructor() {
    init {
        throw AssertionError("No Instances.")
    }

    companion object {
        /**
         * Earth's radius in meters.
         */
        private var EARTH_RADIUS = 6378137.0

        /**
         * Takes two [Point]s and finds the geographic bearing between them.
         *
         * @param point1 first point used for calculating the bearing
         * @param point2 second point used for calculating the bearing
         * @return bearing in decimal degrees
         * @see [Turf Bearing documentation](http://turfjs.org/docs/.bearing)
         *
         * @since 1.3.0
         */
        @JvmStatic
        fun bearing(point1: Point, point2: Point): Double {
            val lon1 = TurfConversion.degreesToRadians(point1.longitude())
            val lon2 = TurfConversion.degreesToRadians(point2.longitude())
            val lat1 = TurfConversion.degreesToRadians(point1.latitude())
            val lat2 = TurfConversion.degreesToRadians(point2.latitude())
            val value1 = sin(lon2 - lon1) * cos(lat2)
            val value2 = cos(lat1) * sin(lat2) - (sin(lat1)
                    * cos(lat2) * cos(lon2 - lon1))
            return TurfConversion.radiansToDegrees(atan2(value1, value2))
        }

        /**
         * Takes a Point and calculates the location of a destination point given a distance in
         * degrees, radians, miles, or kilometers; and bearing in degrees. This uses the Haversine
         * formula to account for global curvature.
         *
         * @param point    starting point used for calculating the destination
         * @param distance distance from the starting point
         * @param bearing  ranging from -180 to 180 in decimal degrees
         * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return destination [Point] result where you specified
         * @see [Turf Destination documetation](http://turfjs.org/docs/.destination)
         *
         * @since 1.2.0
         */
        @JvmStatic
        fun destination(
            point: Point, @FloatRange(from = 0.0) distance: Double,
            @FloatRange(from = -180.0, to = 180.0) bearing: Double,
            @TurfUnitCriteria units: String
        ): Point {
            val longitude1 = TurfConversion.degreesToRadians(point.longitude())
            val latitude1 = TurfConversion.degreesToRadians(point.latitude())
            val bearingRad = TurfConversion.degreesToRadians(bearing)
            val radians = TurfConversion.lengthToRadians(distance, units)
            val latitude2 = asin(
                sin(latitude1) * cos(radians)
                        + cos(latitude1) * sin(radians) * cos(bearingRad)
            )
            val longitude2 = longitude1 + atan2(
                sin(bearingRad)
                        * sin(radians) * cos(latitude1),
                cos(radians) - sin(latitude1) * sin(latitude2)
            )
            return fromLngLat(
                TurfConversion.radiansToDegrees(longitude2),
                TurfConversion.radiansToDegrees(latitude2)
            )
        }
        
        /**
         * Calculates the distance between two points in degress, radians, miles, or kilometers. This
         * uses the Haversine formula to account for global curvature.
         *
         * @param point1 first point used for calculating the bearing
         * @param point2 second point used for calculating the bearing
         * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return distance between the two points in kilometers
         * @see [Turf distance documentation](http://turfjs.org/docs/.distance)
         *
         * @since 1.2.0
         */
        @JvmStatic
        @JvmOverloads
        fun distance(
            point1: Point, point2: Point,
            @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
        ): Double {
            val difLat = TurfConversion.degreesToRadians(point2.latitude() - point1.latitude())
            val difLon = TurfConversion.degreesToRadians(point2.longitude() - point1.longitude())
            val lat1 = TurfConversion.degreesToRadians(point1.latitude())
            val lat2 = TurfConversion.degreesToRadians(point2.latitude())
            val value = (sin(difLat / 2).pow(2.0)
                    + sin(difLon / 2).pow(2.0) * cos(lat1) * cos(lat2))
            return TurfConversion.radiansToLength(
                2 * atan2(sqrt(value), sqrt(1 - value)), units
            )
        }

        /**
         * Takes a [LineString] and measures its length in the specified units.
         *
         * @param lineString geometry to measure
         * @param units      one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return length of the input line in the units specified
         * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
         *
         * @since 1.2.0
         */
        @JvmStatic
        fun length(
            lineString: LineString,
            @TurfUnitCriteria units: String
        ): Double {
            val coordinates = lineString.coordinates()
            return length(coordinates, units)
        }

        /**
         * Takes a [MultiLineString] and measures its length in the specified units.
         *
         * @param multiLineString geometry to measure
         * @param units           one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return length of the input lines combined, in the units specified
         * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
         *
         * @since 1.2.0
         */
        @JvmStatic
        fun length(
            multiLineString: MultiLineString,
            @TurfUnitCriteria units: String
        ): Double {
            var len = 0.0
            for (points in multiLineString.coordinates()) {
                len += length(points, units)
            }
            return len
        }

        /**
         * Takes a [Polygon] and measures its perimeter in the specified units. if the polygon
         * contains holes, the perimeter will also be included.
         *
         * @param polygon geometry to measure
         * @param units   one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return total perimeter of the input polygon in the units specified
         * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
         *
         * @since 1.2.0
         */
        @JvmStatic
        fun length(
            polygon: Polygon,
            @TurfUnitCriteria units: String
        ): Double {
            var len = 0.0
            for (points in polygon.coordinates()) {
                len += length(points, units)
            }
            return len
        }

        /**
         * Takes a [MultiPolygon] and measures each polygons perimeter in the specified units. if
         * one of the polygons contains holes, the perimeter will also be included.
         *
         * @param multiPolygon geometry to measure
         * @param units        one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return total perimeter of the input polygons combined, in the units specified
         * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
         *
         * @since 1.2.0
         */
        @Suppress("unused")
        @JvmStatic
        fun length(
            multiPolygon: MultiPolygon,
            @TurfUnitCriteria units: String
        ): Double {
            var len = 0.0
            val coordinates = multiPolygon.coordinates()
            for (coordinate in coordinates!!) {
                for (theCoordinate in coordinate) {
                    len += length(theCoordinate, units)
                }
            }
            return len
        }

        /**
         * Takes a [List] of [Point] and measures its length in the specified units.
         *
         * @param coords geometry to measure
         * @param units  one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return length of the input line in the units specified
         * @see [Turf Line Distance documentation](http://turfjs.org/docs/.linedistance)
         *
         * @since 5.2.0
         */
        @JvmStatic
        fun length(coords: List<Point>, units: String): Double {
            var travelled = 0.0
            var prevCoords = coords[0]
            var curCoords: Point
            for (i in 1 until coords.size) {
                curCoords = coords[i]
                travelled += distance(prevCoords, curCoords, units)
                prevCoords = curCoords
            }
            return travelled
        }

        /**
         * Takes two [Point]s and returns a point midway between them. The midpoint is calculated
         * geodesically, meaning the curvature of the earth is taken into account.
         *
         * @param from first point used for calculating the midpoint
         * @param to   second point used for calculating the midpoint
         * @return a [Point] midway between point1 and point2
         * @see [Turf Midpoint documentation](http://turfjs.org/docs/.midpoint)
         *
         * @since 1.3.0
         */
        @JvmStatic
        fun midpoint(from: Point, to: Point): Point {
            val dist = distance(from, to, TurfConstants.UNIT_MILES)
            val heading = bearing(from, to)
            return destination(from, dist / 2, heading, TurfConstants.UNIT_MILES)
        }

        /**
         * Takes a line and returns a point at a specified distance along the line.
         *
         * @param line     that the point should be placed upon
         * @param distance along the linestring geometry which the point should be placed on
         * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return a [Point] which is on the linestring provided and at the distance from
         * the origin of that line to the end of the distance
         * @since 1.3.0
         */
        @JvmStatic
        fun along(
            line: LineString, @FloatRange(from = 0.0) distance: Double,
            @TurfUnitCriteria units: String
        ): Point {
            return along(line.coordinates(), distance, units)
        }

        /**
         * Takes a list of points and returns a point at a specified distance along the line.
         *
         * @param coords   that the point should be placed upon
         * @param distance along the linestring geometry which the point should be placed on
         * @param units    one of the units found inside [TurfConstants.TurfUnitCriteria]
         * @return a [Point] which is on the linestring provided and at the distance from
         * the origin of that line to the end of the distance
         * @since 5.2.0
         */
        @JvmStatic
        fun along(
            coords: List<Point>, @FloatRange(from = 0.0) distance: Double,
            @TurfUnitCriteria units: String
        ): Point {
            var travelled = 0.0
            for (i in coords.indices) {
                travelled += if (distance >= travelled && i == coords.size - 1) {
                    break
                } else if (travelled >= distance) {
                    val overshot = distance - travelled
                    return if (overshot == 0.0) {
                        coords[i]
                    } else {
                        val direction = bearing(coords[i], coords[i - 1]) - 180
                        destination(coords[i], overshot, direction, units)
                    }
                } else {
                    distance(coords[i], coords[i + 1], units)
                }
            }
            return coords[coords.size - 1]
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param point a [Point] object
         * @return A double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        @JvmStatic
        fun bbox(point: Point): DoubleArray {
            val resultCoords = TurfMeta.coordAll(point)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param lineString a [LineString] object
         * @return A double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        @JvmStatic
        fun bbox(lineString: LineString): DoubleArray {
            val resultCoords = TurfMeta.coordAll(lineString)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param multiPoint a [MultiPoint] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        @JvmStatic
        fun bbox(multiPoint: MultiPoint): DoubleArray {
            val resultCoords = TurfMeta.coordAll(multiPoint)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param polygon a [Polygon] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        @JvmStatic
        fun bbox(polygon: Polygon): DoubleArray {
            val resultCoords = TurfMeta.coordAll(polygon, false)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param multiLineString a [MultiLineString] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        fun bbox(multiLineString: MultiLineString): DoubleArray {
            val resultCoords = TurfMeta.coordAll(multiLineString)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param multiPolygon a [MultiPolygon] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        fun bbox(multiPolygon: MultiPolygon): DoubleArray {
            val resultCoords = TurfMeta.coordAll(multiPolygon, false)
            return bboxCalculator(resultCoords)
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param geoJson a [GeoJson] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 4.8.0
         */
        @Suppress("USELESS_CAST")
        @JvmStatic
        fun bbox(geoJson: GeoJson): DoubleArray {
            val boundingBox = geoJson.bbox()
            if (boundingBox != null) {
                return doubleArrayOf(
                    boundingBox.west(),
                    boundingBox.south(),
                    boundingBox.east(),
                    boundingBox.north()
                )
            }

            return when (geoJson) {
                is Geometry -> {
                    bbox((geoJson as Geometry))
                }
                is FeatureCollection -> {
                    bbox((geoJson as FeatureCollection))
                }
                is Feature -> {
                    bbox((geoJson as Feature))
                }
                else -> {
                    throw UnsupportedOperationException("bbox type not supported for GeoJson instance")
                }
            }
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param featureCollection a [FeatureCollection] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 4.8.0
         */
        @JvmStatic
        fun bbox(featureCollection: FeatureCollection): DoubleArray {
            return bboxCalculator(TurfMeta.coordAll(featureCollection, false))
        }

        /**
         * Takes a set of features, calculates the bbox of all input features, and returns a bounding box.
         *
         * @param feature a [Feature] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 4.8.0
         */
        @JvmStatic
        fun bbox(feature: Feature): DoubleArray {
            return bboxCalculator(TurfMeta.coordAll(feature, false))
        }

        /**
         * Takes an arbitrary [Geometry] and calculates a bounding box.
         *
         * @param geometry a [Geometry] object
         * @return a double array defining the bounding box in this order `[minX, minY, maxX, maxY]`
         * @since 2.0.0
         */
        @Suppress("USELESS_CAST")
        @JvmStatic
        fun bbox(geometry: Geometry): DoubleArray {
            return if (geometry is Point) {
                bbox((geometry as Point))
            } else if (geometry is MultiPoint) {
                bbox((geometry as MultiPoint))
            } else if (geometry is LineString) {
                bbox((geometry as LineString))
            } else if (geometry is MultiLineString) {
                bbox((geometry as MultiLineString))
            } else if (geometry is Polygon) {
                bbox((geometry as Polygon))
            } else if (geometry is MultiPolygon) {
                bbox((geometry as MultiPolygon))
            } else if (geometry is GeometryCollection) {
                val points: MutableList<Point> = ArrayList()
                for (geo in (geometry as GeometryCollection).geometries()) {
                    // recursive
                    val bbox = bbox(geo)
                    points.add(fromLngLat(bbox[0], bbox[1]))
                    points.add(fromLngLat(bbox[2], bbox[1]))
                    points.add(fromLngLat(bbox[2], bbox[3]))
                    points.add(fromLngLat(bbox[0], bbox[3]))
                }
                bbox(fromLngLats(points))
            } else {
                throw RuntimeException("Unknown geometry class: " + geometry.javaClass)
            }
        }

        private fun bboxCalculator(resultCoords: List<Point?>): DoubleArray {
            val bbox = DoubleArray(4)
            bbox[0] = Double.POSITIVE_INFINITY
            bbox[1] = Double.POSITIVE_INFINITY
            bbox[2] = Double.NEGATIVE_INFINITY
            bbox[3] = Double.NEGATIVE_INFINITY
            for (point in resultCoords) {
                if (bbox[0] > point!!.longitude()) {
                    bbox[0] = point.longitude()
                }
                if (bbox[1] > point.latitude()) {
                    bbox[1] = point.latitude()
                }
                if (bbox[2] < point.longitude()) {
                    bbox[2] = point.longitude()
                }
                if (bbox[3] < point.latitude()) {
                    bbox[3] = point.latitude()
                }
            }
            return bbox
        }
        /**
         * Takes a [BoundingBox] and uses its coordinates to create a [Polygon]
         * geometry.
         *
         * @param boundingBox a [BoundingBox] object to calculate with
         * @param properties a [JsonObject] containing the feature properties
         * @param id  common identifier of this feature
         * @return a [Feature] object
         * @see [Turf BoundingBox Polygon documentation](http://turfjs.org/docs/.bboxPolygon)
         *
         * @since 4.9.0
         */
        @JvmOverloads
        @JvmStatic
        fun bboxPolygon(
            boundingBox: BoundingBox,
            properties: JsonObject? = null,
            id: String? = null
        ): Feature {
            return Feature.fromGeometry(
                fromLngLats(
                    listOf(
                        listOf(
                            fromLngLat(boundingBox.west(), boundingBox.south()),
                            fromLngLat(boundingBox.east(), boundingBox.south()),
                            fromLngLat(boundingBox.east(), boundingBox.north()),
                            fromLngLat(boundingBox.west(), boundingBox.north()),
                            fromLngLat(boundingBox.west(), boundingBox.south())
                        )
                    )
                ), properties, id
            )
        }
        
        /**
         * Takes a bbox and uses its coordinates to create a [Polygon] geometry.
         *
         * @param bbox a double[] object to calculate with
         * @param properties a [JsonObject] containing the feature properties
         * @param id  common identifier of this feature
         * @return a [Feature] object
         * @see [Turf BoundingBox Polygon documentation](http://turfjs.org/docs/.bboxPolygon)
         *
         * @since 4.9.0
         */
        @JvmOverloads
        @JvmStatic
        fun bboxPolygon(
            bbox: DoubleArray,
            properties: JsonObject? = null,
            id: String? = null
        ): Feature {
            return Feature.fromGeometry(
                fromLngLats(
                    listOf(
                        listOf(
                            fromLngLat(bbox[0], bbox[1]),
                            fromLngLat(bbox[2], bbox[1]),
                            fromLngLat(bbox[2], bbox[3]),
                            fromLngLat(bbox[0], bbox[3]),
                            fromLngLat(bbox[0], bbox[1])
                        )
                    )
                ), properties, id
            )
        }

        /**
         * Takes any number of features and returns a rectangular Polygon that encompasses all vertices.
         *
         * @param geoJson input features
         * @return a rectangular Polygon feature that encompasses all vertices
         * @since 4.9.0
         */
        @JvmStatic
        fun envelope(geoJson: GeoJson): Polygon? {
            return bboxPolygon(bbox(geoJson)).geometry() as Polygon?
        }

        /**
         * Takes a bounding box and calculates the minimum square bounding box
         * that would contain the input.
         *
         * @param boundingBox extent in west, south, east, north order
         * @return a square surrounding bbox
         * @since 4.9.0
         */
        @JvmStatic
        fun square(boundingBox: BoundingBox): BoundingBox {
            val horizontalDistance = distance(
                boundingBox.southwest(),
                fromLngLat(boundingBox.east(), boundingBox.south())
            )
            val verticalDistance = distance(
                fromLngLat(boundingBox.west(), boundingBox.south()),
                fromLngLat(boundingBox.west(), boundingBox.north())
            )
            return if (horizontalDistance >= verticalDistance) {
                val verticalMidpoint = (boundingBox.south() + boundingBox.north()) / 2
                fromLngLats(
                    boundingBox.west(),
                    verticalMidpoint - (boundingBox.east() - boundingBox.west()) / 2,
                    boundingBox.east(),
                    verticalMidpoint + (boundingBox.east() - boundingBox.west()) / 2
                )
            } else {
                val horizontalMidpoint = (boundingBox.west() + boundingBox.east()) / 2
                fromLngLats(
                    horizontalMidpoint - (boundingBox.north() - boundingBox.south()) / 2,
                    boundingBox.south(),
                    horizontalMidpoint + (boundingBox.north() - boundingBox.south()) / 2,
                    boundingBox.north()
                )
            }
        }

        /**
         * Takes one [Feature] and returns it's area in square meters.
         *
         * @param feature input [Feature]
         * @return area in square meters
         * @since 4.10.0
         */
        @JvmStatic
        fun area(feature: Feature): Double {
            return if (feature.geometry() != null) area(feature.geometry()!!) else 0.0
        }

        /**
         * Takes one [FeatureCollection] and returns it's area in square meters.
         *
         * @param featureCollection input [FeatureCollection]
         * @return area in square meters
         * @since 4.10.0
         */
        @JvmStatic
        fun area(featureCollection: FeatureCollection): Double {
            val features = featureCollection.features()
            var total = 0.0
            if (features != null) {
                for (feature in features) {
                    total += area(feature)
                }
            }
            return total
        }

        /**
         * Takes one [Geometry] and returns its area in square meters.
         *
         * @param geometry input [Geometry]
         * @return area in square meters
         * @since 4.10.0
         */
        @JvmStatic
        fun area(geometry: Geometry): Double {
            return calculateArea(geometry)
        }

        private fun calculateArea(geometry: Geometry): Double {
            var total = 0.0
            return when(geometry) {
                   is Polygon -> {
                       polygonArea(geometry.coordinates())
                   }
                   is MultiPolygon -> {
                       val coordinates = geometry.coordinates()
                       for (i in coordinates!!.indices) {
                           total += polygonArea(coordinates[i])
                       }
                       total
                   }
                   else -> {
                       // Area should be 0 for case Point, MultiPoint, LineString and MultiLineString
                       0.0
                   }
               }
        }

        private fun polygonArea(coordinates: List<List<Point>>): Double {
            var total = 0.0
            if (coordinates.isNotEmpty()) {
                total += abs(ringArea(coordinates[0]))
                for (i in 1 until coordinates.size) {
                    total -= abs(ringArea(coordinates[i]))
                }
            }
            return total
        }

        /**
         *
         *
         * Calculate the approximate area of the polygon were it projected onto the earth.
         * Note that this area will be positive if ring is oriented clockwise, otherwise
         * it will be negative.
         *
         *
         * Reference:
         * Robert. G. Chamberlain and William H. Duquette, "Some Algorithms for Polygons on a Sphere",
         * JPL Publication 07-03, Jet Propulsion
         * Laboratory, Pasadena, CA, June 2007 [JPL Publication 07-03](https://trs.jpl.nasa.gov/handle/2014/41271)
         *
         * @param coordinates  A list of [Point] of Ring Coordinates
         * @return The approximate signed geodesic area of the polygon in square meters.
         */
        private fun ringArea(coordinates: List<Point>): Double {
            var p1: Point
            var p2: Point
            var p3: Point
            var lowerIndex: Int
            var middleIndex: Int
            var upperIndex: Int
            var total = 0.0
            val coordsLength = coordinates.size
            if (coordsLength > 2) {
                for (i in 0 until coordsLength) {
                    if (i == coordsLength - 2) { // i = N-2
                        lowerIndex = coordsLength - 2
                        middleIndex = coordsLength - 1
                        upperIndex = 0
                    } else if (i == coordsLength - 1) { // i = N-1
                        lowerIndex = coordsLength - 1
                        middleIndex = 0
                        upperIndex = 1
                    } else { // i = 0 to N-3
                        lowerIndex = i
                        middleIndex = i + 1
                        upperIndex = i + 2
                    }
                    p1 = coordinates[lowerIndex]
                    p2 = coordinates[middleIndex]
                    p3 = coordinates[upperIndex]
                    total += (rad(p3.longitude()) - rad(p1.longitude())) * sin(rad(p2.latitude()))
                }
                total = total * EARTH_RADIUS * EARTH_RADIUS / 2
            }
            return total
        }

        private fun rad(num: Double): Double {
            return num * Math.PI / 180
        }

        /**
         * Takes a [Feature] and returns the absolute center of the [Feature].
         *
         * @param feature the single [Feature] to find the center of.
         * @param properties a optional [JsonObject] containing the properties that should be
         * placed in the returned [Feature].
         * @param id  an optional common identifier that should be placed in the returned [Feature].
         * @return a [Feature] with a [Point] geometry type.
         * @since 5.3.0
         */
        @JvmStatic
        fun center(
            feature: Feature?,
            properties: JsonObject?,
            id: String?
        ): Feature {
            return center(FeatureCollection.fromFeature(feature!!), properties, id)
        }

        /**
         * Takes a [Feature] and returns the absolute center of the [Feature].
         *
         * @param feature the single [Feature] to find the center of.
         * @return a [Feature] with a [Point] geometry type.
         * @since 5.3.0
         */
        @Suppress("unused")
        @JvmStatic
        fun center(feature: Feature?): Feature {
            return center(FeatureCollection.fromFeature(feature!!), null, null)
        }
        
        /**
         * Takes [FeatureCollection] and returns the absolute center
         * of the [Feature]s in the [FeatureCollection].
         *
         * @param featureCollection the single [FeatureCollection] to find the center of.
         * @param properties a optional [JsonObject] containing the properties that should be
         * placed in the returned [Feature].
         * @param id  an optional common identifier that should be placed in the returned [Feature].
         * @return a [Feature] with a [Point] geometry type.
         * @since 5.3.0
         */
        @JvmStatic
        @JvmOverloads
        fun center(
            featureCollection: FeatureCollection,
            properties: JsonObject? = null,
            id: String? = null
        ): Feature {
            val ext = bbox(featureCollection)
            val finalCenterLongitude = (ext[0] + ext[2]) / 2
            val finalCenterLatitude = (ext[1] + ext[3]) / 2
            return Feature.fromGeometry(
                fromLngLat(finalCenterLongitude, finalCenterLatitude),
                properties, id
            )
        }
    }
}