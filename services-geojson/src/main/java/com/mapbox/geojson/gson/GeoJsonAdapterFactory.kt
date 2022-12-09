package com.mapbox.geojson.gson

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeometryCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.MultiLineString
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.MultiPolygon
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon

/**
 * A GeoJson type adapter factory for convenience for
 * serialization/deserialization.
 *
 * @since 3.0.0
 */
@Keep
abstract class GeoJsonAdapterFactory : TypeAdapterFactory {
    /**
     * GeoJsonAdapterFactory implementation.
     *
     * @since 3.0.0
     */
    class GeoJsonAdapterFactoryIml : GeoJsonAdapterFactory() {
        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
            val rawType: Class<*> = type.rawType
            if (BoundingBox::class.java.isAssignableFrom(rawType)) {
                return BoundingBox.typeAdapter(gson) as TypeAdapter<T>
            } else if (Feature::class.java.isAssignableFrom(rawType)) {
                return Feature.typeAdapter(gson) as TypeAdapter<T>
            } else if (FeatureCollection::class.java.isAssignableFrom(rawType)) {
                return FeatureCollection.typeAdapter(gson) as TypeAdapter<T>
            } else if (GeometryCollection::class.java.isAssignableFrom(rawType)) {
                return GeometryCollection.typeAdapter(gson) as TypeAdapter<T>
            } else if (LineString::class.java.isAssignableFrom(rawType)) {
                return LineString.typeAdapter(gson) as TypeAdapter<T>
            } else if (MultiLineString::class.java.isAssignableFrom(rawType)) {
                return MultiLineString.typeAdapter(gson) as TypeAdapter<T>
            } else if (MultiPoint::class.java.isAssignableFrom(rawType)) {
                return MultiPoint.typeAdapter(gson) as TypeAdapter<T>
            } else if (MultiPolygon::class.java.isAssignableFrom(rawType)) {
                return MultiPolygon.typeAdapter(gson) as TypeAdapter<T>
            } else if (Polygon::class.java.isAssignableFrom(rawType)) {
                return Polygon.typeAdapter(gson) as TypeAdapter<T>
            } else if (Point::class.java.isAssignableFrom(rawType)) {
                return Point.typeAdapter(gson) as TypeAdapter<T>
            }
            return null
        }
    }

    companion object {
        /**
         * Create a new instance of this GeoJson type adapter factory, this is passed into the Gson
         * Builder.
         *
         * @return a new GSON TypeAdapterFactory
         * @since 3.0.0
         */
        fun create(): TypeAdapterFactory {
            return GeoJsonAdapterFactoryIml()
        }
    }
}