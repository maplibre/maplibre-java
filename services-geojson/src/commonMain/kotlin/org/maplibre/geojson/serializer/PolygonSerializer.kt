package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Polygon

/**
 * Internal JSON serializer for [Polygon] model.
 *
 * @see Polygon
 */
@OptIn(ExperimentalSerializationApi::class)
internal object PolygonSerializer : KSerializer<Polygon> {
    private val polygonCoordinatesSerializer = PolygonCoordinatesSerializer
    private val boundingBoxSerializer = BoundingBoxSerializer

    override val descriptor = buildClassSerialDescriptor("Polygon") {
        element("coordinates", polygonCoordinatesSerializer.descriptor)
        element("bbox", boundingBoxSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Polygon) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, polygonCoordinatesSerializer, value)
            encodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer, value.boundingBox)
        }
    }

    override fun deserialize(decoder: Decoder): Polygon {
        return decoder.decodeStructure(descriptor) {
            var polygon: Polygon? = null
            var boundingBox: BoundingBox? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> polygon = decodeSerializableElement(descriptor, 0, polygonCoordinatesSerializer)
                    1 -> boundingBox = decodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            requireNotNull(polygon)
            polygon.copy(boundingBox = boundingBox)
        }
    }
}