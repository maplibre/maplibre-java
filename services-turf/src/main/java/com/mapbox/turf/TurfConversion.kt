package com.mapbox.turf

import androidx.annotation.FloatRange
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.LineString
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiLineString.Companion.fromLineStrings
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.MultiPolygon.Companion.fromPolygons
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.turf.TurfConstants.TurfUnitCriteria

/**
 * This class is made up of methods that take in an object, convert it, and then return the object
 * in the desired units or object.
 *
 * @see [Turfjs documentation](http://turfjs.org/docs/)
 *
 * @since 1.2.0
 */
object TurfConversion {
    private val FACTORS: MutableMap<String?, Double> = HashMap()

    init {
        FACTORS[TurfConstants.UNIT_MILES] = 3960.0
        FACTORS[TurfConstants.UNIT_NAUTICAL_MILES] = 3441.145
        FACTORS[TurfConstants.UNIT_DEGREES] = 57.2957795
        FACTORS[TurfConstants.UNIT_RADIANS] = 1.0
        FACTORS[TurfConstants.UNIT_INCHES] = 250905600.0
        FACTORS[TurfConstants.UNIT_YARDS] = 6969600.0
        FACTORS[TurfConstants.UNIT_METERS] = 6373000.0
        FACTORS[TurfConstants.UNIT_CENTIMETERS] = 6.373e+8
        FACTORS[TurfConstants.UNIT_KILOMETERS] = 6373.0
        FACTORS[TurfConstants.UNIT_FEET] = 20908792.65
        FACTORS[TurfConstants.UNIT_CENTIMETRES] = 6.373e+8
        FACTORS[TurfConstants.UNIT_METRES] = 6373000.0
        FACTORS[TurfConstants.UNIT_KILOMETRES] = 6373.0
    }

    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into degrees
     * Valid units: miles, nauticalmiles, inches, yards, meters, metres, centimeters, kilometres,
     * feet.
     *
     * @param distance in real units
     * @param units    can be degrees, radians, miles, or kilometers inches, yards, metres, meters,
     * kilometres, kilometers.
     * @return a double value representing the distance in degrees
     * @since 3.0.0
     */
    @JvmStatic
    fun lengthToDegrees(distance: Double, @TurfUnitCriteria units: String): Double {
        return radiansToDegrees(lengthToRadians(distance, units))
    }

    /**
     * Converts an angle in degrees to radians.
     *
     * @param degrees angle between 0 and 360 degrees
     * @return angle in radians
     * @since 3.1.0
     */
    fun degreesToRadians(degrees: Double): Double {
        val radians = degrees % 360
        return radians * Math.PI / 180
    }

    /**
     * Converts an angle in radians to degrees.
     *
     * @param radians angle in radians
     * @return degrees between 0 and 360 degrees
     * @since 3.0.0
     */
    fun radiansToDegrees(radians: Double): Double {
        val degrees = radians % (2 * Math.PI)
        return degrees * 180 / Math.PI
    }
    /**
     * Convert a distance measurement (assuming a spherical Earth) from radians to a more friendly
     * unit.
     *
     * @param radians a double using unit radian
     * @param units   pass in one of the units defined in [TurfUnitCriteria]
     * @return converted radian to distance value
     * @since 1.2.0
     */
    /**
     * Convert a distance measurement (assuming a spherical Earth) from radians to a more friendly
     * unit. The units used here equals the default.
     *
     * @param radians a double using unit radian
     * @return converted radian to distance value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun radiansToLength(
        radians: Double,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return radians * FACTORS!![units]!!
    }
    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into
     * radians.
     *
     * @param distance double representing a distance value
     * @param units    pass in one of the units defined in [TurfUnitCriteria]
     * @return converted distance to radians value
     * @since 1.2.0
     */
    /**
     * Convert a distance measurement (assuming a spherical Earth) from a real-world unit into
     * radians.
     *
     * @param distance double representing a distance value assuming the distance units is in
     * kilometers
     * @return converted distance to radians value
     * @since 1.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun lengthToRadians(
        distance: Double,
        @TurfUnitCriteria units: String = TurfConstants.UNIT_DEFAULT
    ): Double {
        return distance / FACTORS!![units]!!
    }
    /**
     * Converts a distance to a different unit specified.
     *
     * @param distance     the distance to be converted
     * @param originalUnit of the distance, must be one of the units defined in
     * [TurfUnitCriteria]
     * @param finalUnit    returned unit, [TurfConstants.UNIT_DEFAULT] if not specified
     * @return the converted distance
     * @since 2.2.0
     */
    /**
     * Converts a distance to the default units. Use
     * [TurfConversion.convertLength] to specify a unit to convert to.
     *
     * @param distance     double representing a distance value
     * @param originalUnit of the distance, must be one of the units defined in
     * [TurfUnitCriteria]
     * @return converted distance in the default unit
     * @since 2.2.0
     */
    @JvmStatic
    @JvmOverloads
    fun convertLength(
        @FloatRange(from = 0.0) distance: Double,
        @TurfUnitCriteria originalUnit: String,
        @TurfUnitCriteria finalUnit: String? = TurfConstants.UNIT_DEFAULT
    ): Double {
        var finalUnit = finalUnit
        if (finalUnit == null) {
            finalUnit = TurfConstants.UNIT_DEFAULT
        }
        return radiansToLength(lengthToRadians(distance, originalUnit), finalUnit)
    }

    /**
     * Takes a [FeatureCollection] and
     * returns all positions as [Point] objects.
     *
     * @param featureCollection a [FeatureCollection] object
     * @return a new [FeatureCollection] object with [Point] objects
     * @since 4.8.0
     */
    fun explode(featureCollection: FeatureCollection): FeatureCollection {
        val finalFeatureList: MutableList<Feature> = ArrayList()
        for (singlePoint in TurfMeta.coordAll(featureCollection, true)) {
            finalFeatureList.add(Feature.fromGeometry(singlePoint))
        }
        return fromFeatures(finalFeatureList)
    }

    /**
     * Takes a [Feature]  and
     * returns its position as a [Point] objects.
     *
     * @param feature a [Feature] object
     * @return a new [FeatureCollection] object with [Point] objects
     * @since 4.8.0
     */
    fun explode(feature: Feature): FeatureCollection {
        val finalFeatureList: MutableList<Feature> = ArrayList()
        for (singlePoint in TurfMeta.coordAll(feature, true)) {
            finalFeatureList.add(Feature.fromGeometry(singlePoint))
        }
        return fromFeatures(finalFeatureList)
    }
    /**
     * Takes a [Feature] that contains [Polygon] and a properties [JsonObject] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [Polygon]
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    /**
     * Takes a [Feature] that contains [Polygon] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [Polygon]
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmOverloads
    fun polygonToLine(feature: Feature, properties: JsonObject? = null): Feature? {
        val geometry = feature.geometry()
        if (geometry is Polygon) {
            return polygonToLine(
                geometry,
                properties
                    ?: if (feature.type() == "Feature") feature.properties() else JsonObject()
            )
        }
        throw TurfException("Feature's geometry must be Polygon")
    }
    /**
     * Takes a [Polygon] and a properties [JsonObject] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param polygon a [Polygon] object
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    /**
     * Takes a [Polygon] and
     * covert it to a [Feature] that contains [LineString] or [MultiLineString].
     *
     * @param polygon a [Polygon] object
     * @return  a [Feature] object that contains [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmOverloads
    fun polygonToLine(polygon: Polygon, properties: JsonObject? = null): Feature? {
        return coordsToLine(polygon.coordinates(), properties)
    }
    /**
     * Takes a [MultiPolygon] and a properties [JsonObject] and
     * covert it to a [FeatureCollection] that contains list
     * of [Feature] of [LineString] or [MultiLineString].
     *
     * @param multiPolygon a [MultiPolygon] object
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    /**
     * Takes a [MultiPolygon] and
     * covert it to a [FeatureCollection] that contains list
     * of [Feature] of [LineString] or [MultiLineString].
     *
     * @param multiPolygon a [MultiPolygon] object
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmOverloads
    fun polygonToLine(
        multiPolygon: MultiPolygon,
        properties: JsonObject? = null
    ): FeatureCollection {
        val coordinates = multiPolygon.coordinates()
        val finalFeatureList: MutableList<Feature> = ArrayList()
        for (polygonCoordinates in coordinates!!) {
            finalFeatureList.add(coordsToLine(polygonCoordinates, properties)!!)
        }
        return fromFeatures(finalFeatureList)
    }
    /**
     * Takes a [Feature] that contains [MultiPolygon] and a
     * properties [JsonObject] and
     * covert it to a [FeatureCollection] that contains
     * list of [Feature] of [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [MultiPolygon]
     * @param properties a [JsonObject] that represents a feature's properties
     * @return  a [FeatureCollection] object that contains
     * list of [Feature] of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    /**
     * Takes a [Feature] that contains [MultiPolygon] and
     * covert it to a [FeatureCollection] that contains list of [Feature]
     * of [LineString] or [MultiLineString].
     *
     * @param feature a [Feature] object that contains [Polygon]
     * @return  a [FeatureCollection] object that contains list of [Feature]
     * of [LineString] or [MultiLineString]
     * @since 4.9.0
     */
    @JvmOverloads
    fun multiPolygonToLine(
        feature: Feature,
        properties: JsonObject? = null
    ): FeatureCollection {
        val geometry = feature.geometry()
        if (geometry is MultiPolygon) {
            return polygonToLine(
                geometry,
                properties
                    ?: if (feature.type() == "Feature") feature.properties() else JsonObject()
            )
        }
        throw TurfException("Feature's geometry must be MultiPolygon")
    }

    private fun coordsToLine(
        coordinates: List<List<Point>>,
        properties: JsonObject?
    ): Feature? {
        if (coordinates.size > 1) {
            return Feature.fromGeometry(MultiLineString.fromLngLats(coordinates), properties)
        } else if (coordinates.size == 1) {
            val lineString = LineString.fromLngLats(coordinates[0])
            return Feature.fromGeometry(lineString, properties)
        }
        return null
    }

    /**
     *
     *
     * Combines a FeatureCollection of geometries and returns
     * a [FeatureCollection] with "Multi-" geometries in it.
     * If the original FeatureCollection parameter has [Point](s)
     * and/or [MultiPoint]s), the returned
     * FeatureCollection will include a [MultiPoint] object.
     *
     *
     * If the original FeatureCollection parameter has
     * [LineString](s) and/or [MultiLineString]s), the returned
     * FeatureCollection will include a [MultiLineString] object.
     *
     *
     * If the original FeatureCollection parameter has
     * [Polygon](s) and/or [MultiPolygon]s), the returned
     * FeatureCollection will include a [MultiPolygon] object.
     *
     * @param originalFeatureCollection a [FeatureCollection]
     *
     * @return a [FeatureCollection] with a "Multi-" geometry
     * or "Multi-" geometries.
     *
     * @since 4.10.0
     */
    @JvmStatic
    fun combine(originalFeatureCollection: FeatureCollection): FeatureCollection {
        if (originalFeatureCollection.features() == null) {
            throw TurfException("Your FeatureCollection is null.")
        } else if (originalFeatureCollection.features()!!.size == 0) {
            throw TurfException("Your FeatureCollection doesn't have any Feature objects in it.")
        }
        val pointList: MutableList<Point> = ArrayList(0)
        val lineStringList: MutableList<LineString> = ArrayList(0)
        val polygonList: MutableList<Polygon> = ArrayList(0)
        for (singleFeature in originalFeatureCollection.features()!!) {
            val singleFeatureGeometry = singleFeature.geometry()
            if (singleFeatureGeometry is Point || singleFeatureGeometry is MultiPoint) {
                if (singleFeatureGeometry is Point) {
                    pointList.add(singleFeatureGeometry)
                } else {
                    pointList.addAll((singleFeatureGeometry as MultiPoint).coordinates())
                }
            } else if (singleFeatureGeometry is LineString || singleFeatureGeometry is MultiLineString) {
                if (singleFeatureGeometry is LineString) {
                    lineStringList.add(singleFeatureGeometry)
                } else {
                    lineStringList.addAll((singleFeatureGeometry as MultiLineString).lineStrings())
                }
            } else if (singleFeatureGeometry is Polygon || singleFeatureGeometry is MultiPolygon) {
                if (singleFeatureGeometry is Polygon) {
                    polygonList.add(singleFeatureGeometry)
                } else {
                    polygonList.addAll((singleFeatureGeometry as MultiPolygon).polygons())
                }
            }
        }
        val finalFeatureList: MutableList<Feature> = ArrayList(0)
        if (!pointList.isEmpty()) {
            finalFeatureList.add(Feature.fromGeometry(MultiPoint.fromLngLats(pointList)))
        }
        if (!lineStringList.isEmpty()) {
            finalFeatureList.add(Feature.fromGeometry(fromLineStrings(lineStringList)))
        }
        if (!polygonList.isEmpty()) {
            finalFeatureList.add(Feature.fromGeometry(fromPolygons(polygonList)))
        }
        return if (finalFeatureList.isEmpty()) originalFeatureCollection else fromFeatures(
            finalFeatureList
        )
    }
}