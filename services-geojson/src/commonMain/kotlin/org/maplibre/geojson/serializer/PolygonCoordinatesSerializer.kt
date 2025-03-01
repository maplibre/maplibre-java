package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.model.Polygon

/**
 * Internal JSON serializer for [Polygon] coordinates arrays.
 *
 * @see Polygon
 */
@OptIn(ExperimentalSerializationApi::class)
internal class PolygonCoordinatesSerializer : KSerializer<Polygon> {
    private val lineStringCoordinatesListSerializer = ListSerializer(LineStringCoordinatesSerializer())

    override val descriptor = SerialDescriptor("Polygon", lineStringCoordinatesListSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Polygon) {
        encoder.encodeSerializableValue(lineStringCoordinatesListSerializer, listOf(value.outerLineStringRing) + value.holeLineStringRings)
    }

    override fun deserialize(decoder: Decoder): Polygon {
        val lineStrings = decoder.decodeSerializableValue(lineStringCoordinatesListSerializer)
        return Polygon(lineStrings.first(), lineStrings.drop(1))
    }
}
