package com.mapbox.turf

import com.google.gson.JsonParser
import org.hamcrest.Matchers
import org.junit.Assert
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

open class TestUtils {
    fun compareJson(expectedJson: String?, actualJson: String?) {
        Assert.assertThat(
            JsonParser.parseString(actualJson),
            Matchers.equalTo(JsonParser.parseString(expectedJson))
        )
    }

    protected fun loadJsonFixture(filename: String): String {
        return try {
            val filepath = "src/test/resources/$filename"
            val encoded = Files.readAllBytes(Paths.get(filepath))
            String(encoded, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            Assert.fail("Unable to load $filename")
            ""
        }
    }

    protected fun getResourceFolderFileNames(folder: String?): List<String> {
        val loader = javaClass.classLoader
        val url = loader.getResource(folder)!!
        val path = url.path
        val names: MutableList<String> = ArrayList()
        for (file in File(path).listFiles()) {
            names.add(file.name)
        }
        return names
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