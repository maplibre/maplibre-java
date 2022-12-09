package com.mapbox.geojson

import com.google.gson.JsonParser
import org.hamcrest.Matchers
import org.junit.Assert
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.*

open class TestUtils {
    fun compareJson(expectedJson: String?, actualJson: String?) {
        Assert.assertThat(
            JsonParser.parseString(actualJson),
            Matchers.equalTo(JsonParser.parseString(expectedJson))
        )
    }

    @Throws(IOException::class)
    protected fun loadJsonFixture(filename: String?): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(filename)!!
        val scanner = Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")
        return if (scanner.hasNext()) scanner.next() else ""
    }

    companion object {
        const val DELTA = 1E-10
        const val ACCESS_TOKEN = "pk.XXX"
        @Throws(IOException::class)
        fun <T : Serializable?> serialize(obj: T): ByteArray {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            oos.close()
            return baos.toByteArray()
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        fun <T : Serializable?> deserialize(bytes: ByteArray?, cl: Class<T>): T {
            val bais = ByteArrayInputStream(bytes)
            val ois = ObjectInputStream(bais)
            val `object` = ois.readObject()
            return cl.cast(`object`)
        }

        /**
         * Comes from Google Utils Test Case
         */
        fun expectNearNumber(expected: Double, actual: Double, epsilon: Double) {
            Assert.assertTrue(
                String.format("Expected %f to be near %f", actual, expected),
                Math.abs(expected - actual) <= epsilon
            )
        }
    }
}