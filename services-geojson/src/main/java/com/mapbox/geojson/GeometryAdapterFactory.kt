package com.mapbox.geojson

import androidx.annotation.Keep
import com.google.gson.TypeAdapterFactory
import com.mapbox.geojson.internal.typeadapters.RuntimeTypeAdapterFactory

/**
 * A Geometry type adapter factory for convenience for serialization/deserialization.
 * @since 4.6.0
 */
@Keep
abstract class GeometryAdapterFactory : TypeAdapterFactory {

    companion object {
        private var geometryTypeFactory: TypeAdapterFactory? = null
        /**
         * Create a new instance of Geometry type adapter factory, this is passed into the Gson
         * Builder.
         *
         * @return a new GSON TypeAdapterFactory
         * @since 4.4.0
         */
        @JvmStatic
        fun create(): TypeAdapterFactory? {
            if (geometryTypeFactory == null) {
                geometryTypeFactory = RuntimeTypeAdapterFactory.of(
                    Geometry::class.java, "type", true
                )
                    .registerSubtype(GeometryCollection::class.java, "GeometryCollection")
                    .registerSubtype(Point::class.java, "Point")
                    .registerSubtype(MultiPoint::class.java, "MultiPoint")
                    .registerSubtype(LineString::class.java, "LineString")
                    .registerSubtype(MultiLineString::class.java, "MultiLineString")
                    .registerSubtype(Polygon::class.java, "Polygon")
                    .registerSubtype(MultiPolygon::class.java, "MultiPolygon")
            }
            return geometryTypeFactory
        }
    }
}