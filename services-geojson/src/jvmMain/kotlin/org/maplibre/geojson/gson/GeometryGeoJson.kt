package org.maplibre.geojson.gson

import org.maplibre.geojson.Geometry

/**
 * This is a utility class that helps create a Geometry instance from a JSON string.
 * @since 4.0.0
 */
@Deprecated(
    message = "This helper class is obsolete. Use new common model instead.",
    replaceWith = ReplaceWith("Geometry", "org.maplibre.geojson.model.Geometry"),
)
object GeometryGeoJson {

    /**
     * Create a new instance of Geometry class by passing in a formatted valid JSON String.
     *
     * @param json a formatted valid JSON string defining a GeoJson Geometry
     * @return a new instance of Geometry class defined by the values passed inside
     *   this static factory method
     * @since 4.0.0
     */
    @JvmStatic
    fun fromJson(json: String): Geometry {
        return Geometry.fromJson(json)
    }
}
