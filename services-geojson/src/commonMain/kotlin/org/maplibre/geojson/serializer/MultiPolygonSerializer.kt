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
import org.maplibre.geojson.model.MultiPolygon
import org.maplibre.geojson.model.Polygon

/**
 * Internal JSON serializer for [MultiPolygon] model.
 *
 * @see MultiPolygon
 */
@OptIn(ExperimentalSerializationApi::class)
internal class MultiPolygonSerializer : KSerializer<MultiPolygon> {
    private val polygonsCoordinatesListSerializer = ListSerializer(PolygonCoordinatesSerializer())
    private val boundingBoxSerializer = BoundingBoxSerializer()

    override val descriptor = buildClassSerialDescriptor("MultiPolygon") {
        element("coordinates", polygonsCoordinatesListSerializer.descriptor)
        element("bbox", boundingBoxSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: MultiPolygon) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, polygonsCoordinatesListSerializer, value.polygons)
            encodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer, value.boundingBox)
        }
    }

    override fun deserialize(decoder: Decoder): MultiPolygon {
        return decoder.decodeStructure(descriptor) {
            var polygons: List<Polygon> = emptyList()
            var boundingBox: BoundingBox? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> polygons = decodeSerializableElement(descriptor, 0, polygonsCoordinatesListSerializer)
                    1 -> boundingBox =
                        decodeNullableSerializableElement(descriptor, 1, boundingBoxSerializer)

                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            MultiPolygon(polygons, boundingBox)
        }
    }
}