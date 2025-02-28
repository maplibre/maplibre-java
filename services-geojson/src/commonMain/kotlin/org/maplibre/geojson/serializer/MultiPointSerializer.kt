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
import org.maplibre.geojson.model.MultiPoint
import org.maplibre.geojson.model.Point

@OptIn(ExperimentalSerializationApi::class)
internal class MultiPointSerializer : KSerializer<MultiPoint> {
    private val pointListSerializer = ListSerializer(PointCoordinatesSerializer())
    private val boundingBoxSerializer = BoundingBoxSerializer()

    override val descriptor = buildClassSerialDescriptor("MultiPoint") {
        element("coordinates", pointListSerializer.descriptor)
        element("bbox", boundingBoxSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MultiPoint) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, pointListSerializer, value.points)
            encodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer, value.boundingBox)
        }
    }

    override fun deserialize(decoder: Decoder): MultiPoint {
        return decoder.decodeStructure(descriptor) {
            var points: List<Point> = emptyList()
            var boundingBox: BoundingBox? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> points = decodeSerializableElement(descriptor, 0, pointListSerializer)
                    1 -> boundingBox =
                        decodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            MultiPoint(points, boundingBox)
        }
    }
}