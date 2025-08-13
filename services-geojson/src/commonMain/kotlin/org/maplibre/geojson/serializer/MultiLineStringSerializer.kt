package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.MultiLineString

/**
 * Internal JSON serializer for [MultiLineString] model.
 *
 * @see MultiLineString
 */
@OptIn(ExperimentalSerializationApi::class)
internal object MultiLineStringSerializer : KSerializer<MultiLineString> {
    private val lineStringCoordinatesListSerializer = ListSerializer(LineStringCoordinatesSerializer)
    private val boundingBoxSerializer = BoundingBoxSerializer

    override val descriptor = buildClassSerialDescriptor("MultiLineString") {
        element("coordinates", lineStringCoordinatesListSerializer.descriptor)
        element("bbox", boundingBoxSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MultiLineString) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, lineStringCoordinatesListSerializer, value.lineStrings)
            encodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer, value.boundingBox)
        }
    }

    override fun deserialize(decoder: Decoder): MultiLineString {
        return decoder.decodeStructure(descriptor) {
            var lineStrings: List<LineString> = emptyList()
            var boundingBox: BoundingBox? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> lineStrings = decodeSerializableElement(descriptor, 0, lineStringCoordinatesListSerializer)
                    1 -> boundingBox =
                        decodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            MultiLineString(lineStrings, boundingBox)
        }
    }
}