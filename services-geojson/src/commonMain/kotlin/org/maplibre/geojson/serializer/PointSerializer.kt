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
import org.maplibre.geojson.model.Point

/**
 * Internal JSON serializer for [Point] model.
 *
 * @see Point
 */
@OptIn(ExperimentalSerializationApi::class)
internal object PointSerializer : KSerializer<Point> {
    private val pointCoordinatesSerializer = PointCoordinatesSerializer
    private val boundingBoxSerializer = BoundingBoxSerializer

    override val descriptor = buildClassSerialDescriptor("Point") {
        element("coordinates", pointCoordinatesSerializer.descriptor)
        element("bbox", boundingBoxSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Point) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, pointCoordinatesSerializer, value)
            encodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer, value.boundingBox)
        }
    }

    override fun deserialize(decoder: Decoder): Point {
        return decoder.decodeStructure(descriptor) {
            var point: Point? = null
            var boundingBox: BoundingBox? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> point = decodeSerializableElement(descriptor, 0, pointCoordinatesSerializer)
                    1 -> boundingBox = decodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            requireNotNull(point)
            point.copy(boundingBox = boundingBox)
        }
    }
}