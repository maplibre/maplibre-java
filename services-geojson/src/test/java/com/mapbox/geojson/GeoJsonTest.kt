package com.mapbox.geojson

import com.mapbox.geojson.FeatureCollection.Companion.fromFeatures
import com.mapbox.geojson.Point.Companion.fromLngLat
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class GeoJsonTest : TestUtils() {
    /**
     * Test whether we are rounding correctly to conform to the RFC 7946 GeoJson spec.
     *
     * @throws IOException If fixture fails loading.
     * @see [section 3.1.10](https://tools.ietf.org/html/rfc7946.section-3.1.10)
     */
    @Test
    @Throws(IOException::class)
    fun testSevenDigitRounding() {
        val roundDown = fromLngLat(1.12345678, 1.12345678)
        val noRound = fromLngLat(1.1234, 1.12345)
        val matchRound = fromLngLat(1.1234567, 1.1234567)
        val roundLat = fromLngLat(1.1234567, 1.12345678)
        val roundLon = fromLngLat(1.12345678, 1.1234567)
        val largeRound = fromLngLat(105.12345678, 89.1234567)
        val negRound = fromLngLat(-105.12345678, -89.1234567)
        val features: MutableList<Feature> = ArrayList()
        features.add(Feature.fromGeometry(roundDown))
        features.add(Feature.fromGeometry(noRound))
        features.add(Feature.fromGeometry(matchRound))
        features.add(Feature.fromGeometry(roundLat))
        features.add(Feature.fromGeometry(roundLon))
        features.add(Feature.fromGeometry(largeRound))
        features.add(Feature.fromGeometry(negRound))
        val featureCollection = fromFeatures(features)
        val featureCollectionRounded = FeatureCollection.fromJson(
            featureCollection.toJson()!!
        ).features()
        val roundDown2 = featureCollectionRounded!![0].geometry() as Point?
        val noRound2 = featureCollectionRounded[1].geometry() as Point?
        val matchRound2 = featureCollectionRounded[2].geometry() as Point?
        val roundLat2 = featureCollectionRounded[3].geometry() as Point?
        val roundLon2 = featureCollectionRounded[4].geometry() as Point?
        val largeRound2 = featureCollectionRounded[5].geometry() as Point?
        val negRound2 = featureCollectionRounded[6].geometry() as Point?
        Assert.assertEquals(1.1234568, roundDown2!!.longitude(), DELTA)
        Assert.assertEquals(1.1234568, roundDown2.latitude(), DELTA)
        Assert.assertEquals(noRound, noRound2)
        Assert.assertEquals(matchRound, matchRound2)
        Assert.assertEquals(
            roundLat.longitude(),
            roundLat2!!.longitude(),
            DELTA
        )
        Assert.assertEquals(1.1234568, roundLat2.latitude(), DELTA)
        Assert.assertEquals(1.1234568, roundLon2!!.longitude(), DELTA)
        Assert.assertEquals(roundLon.latitude(), roundLon2.latitude(), DELTA)
        Assert.assertEquals(105.1234568, largeRound2!!.longitude(), DELTA)
        Assert.assertEquals(
            largeRound.latitude(),
            largeRound2.latitude(),
            DELTA
        )
        Assert.assertEquals(-105.1234568, negRound2!!.longitude(), DELTA)
        Assert.assertEquals(negRound.latitude(), negRound2.latitude(), DELTA)
    }

}