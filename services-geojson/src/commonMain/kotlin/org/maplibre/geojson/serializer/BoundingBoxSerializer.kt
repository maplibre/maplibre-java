package org.maplibre.geojson.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.maplibre.geojson.model.BoundingBox
import org.maplibre.geojson.model.Point

/**
 * Internal JSON serializer for [BoundingBox] model.
 *
 * @see BoundingBox
 */
internal class BoundingBoxSerializer : KSerializer<BoundingBox> {
    private val delegateSerializer = DoubleArraySerializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("BoundingBox", delegateSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: BoundingBox) {
        val includeAltitude = value.southwest.altitude != null && value.northeast.altitude != null
        val data = if (includeAltitude) {
            doubleArrayOf(
                value.southwest.longitude,
                value.southwest.latitude,
                value.southwest.altitude!!,
                value.northeast.longitude,
                value.northeast.latitude,
                value.northeast.altitude!!
            )
        } else {
            doubleArrayOf(
                value.southwest.longitude,
                value.southwest.latitude,
                value.northeast.longitude,
                value.northeast.latitude,
            )
        }

        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): BoundingBox {
        val array = decoder.decodeSerializableValue(delegateSerializer)

        require(array.size == 4 || array.size == 6) {
            "Expected 4 or 6 elements for BoundingBox, but array has ${array.size}"
        }

        return if (array.size == 4) {
            BoundingBox(
                southwest = Point(
                    longitude = array[0],
                    latitude = array[1],
                ),
                northeast = Point(
                    longitude = array[2],
                    latitude = array[3],
                ),
            )
        } else {
            BoundingBox(
                southwest = Point(
                    longitude = array[0],
                    latitude = array[1],
                    altitude = array[2]
                ),
                northeast = Point(
                    longitude = array[3],
                    latitude = array[4],
                    altitude = array[5]
                ),
            )
        }
    }
}