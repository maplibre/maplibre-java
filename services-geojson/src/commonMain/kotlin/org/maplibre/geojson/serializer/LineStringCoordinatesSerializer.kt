package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.LineString
import org.maplibre.geojson.model.Point

@OptIn(ExperimentalSerializationApi::class)
internal class LineStringCoordinatesSerializer : KSerializer<LineString> {
    private val pointCoordinatesListSerializer = ListSerializer(PointCoordinatesSerializer())

    override val descriptor = SerialDescriptor("LineString", pointCoordinatesListSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: LineString) {
        encoder.encodeSerializableValue(pointCoordinatesListSerializer, value.points)
    }

    override fun deserialize(decoder: Decoder): LineString {
        val pointList = decoder.decodeSerializableValue(pointCoordinatesListSerializer)
        return LineString(pointList)
    }
}
