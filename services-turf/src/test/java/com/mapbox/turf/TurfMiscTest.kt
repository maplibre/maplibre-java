package com.mapbox.turf

import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.LineString.Companion.fromLngLats
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.Companion.fromLngLat
import com.mapbox.turf.TurfMeasurement.Companion.along
import com.mapbox.turf.TurfMeasurement.Companion.distance
import com.mapbox.turf.TurfMeasurement.Companion.length
import com.mapbox.turf.TurfMisc.Companion.lineSlice
import com.mapbox.turf.TurfMisc.Companion.lineSliceAlong
import com.mapbox.turf.TurfMisc.Companion.nearestPointOnLine
import junit.framework.TestCase
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.IOException
import java.util.*

class TurfMiscTest : TestUtils() {
    @Rule
    var thrown = ExpectedException.none()
    @Test
    @Throws(Exception::class)
    fun lineSlice_throwsStartStopPointException() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(
            CoreMatchers.startsWith(
                "Turf lineSlice requires a LineString made up of at least 2 "
                        + "coordinates."
            )
        )
        val coords: MutableList<Point> = ArrayList()
        coords.add(fromLngLat(1.0, 1.0))
        val point = fromLngLat(1.0, 1.0)
        val point2 = fromLngLat(2.0, 2.0)
        val lineString = fromLngLats(coords)
        lineSlice(point, point2, lineString)
    }

    @Test
    @Throws(Exception::class)
    fun lineSlice_throwLineMustContainTwoOrMorePoints() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(
            CoreMatchers.startsWith(
                "Start and stop points in Turf lineSlice cannot equal each "
                        + "other."
            )
        )
        val coords: MutableList<Point> = ArrayList()
        coords.add(fromLngLat(1.0, 1.0))
        coords.add(fromLngLat(2.0, 2.0))
        val point = fromLngLat(1.0, 1.0)
        val lineString = fromLngLats(coords)
        lineSlice(point, point, lineString)
    }

    @Test
    @Throws(Exception::class)
    fun lineSlice_returnsEmptyLineStringRatherThanNull() {
        val coords: MutableList<Point> = ArrayList()
        coords.add(fromLngLat(1.0, 1.0))
        coords.add(fromLngLat(2.0, 2.0))
        val lineString = fromLngLats(coords)
        TestCase.assertNotNull(lineSlice(coords[0], coords[1], lineString))
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfLineSliceLine1() {
        val start = fromLngLat(-97.79617309570312, 22.254624939561698)
        val stop = fromLngLat(-97.72750854492188, 22.057641623615734)
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ONE))
        val sliced = lineSlice(start, stop, line1)
        TestCase.assertNotNull(sliced)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfLineSliceRawGeometry() {
        val start = fromLngLat(-97.79617309570312, 22.254624939561698)
        val stop = fromLngLat(-97.72750854492188, 22.057641623615734)
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ONE))
        val sliced = lineSlice(start, stop, (line1.geometry() as LineString?)!!)
        TestCase.assertNotNull(sliced)
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfLineSliceLine2() {
        val start = fromLngLat(0.0, 0.1)
        val stop = fromLngLat(.9, .8)
        val coordinates = ArrayList<Point>()
        coordinates.add(fromLngLat(0.0, 0.0))
        coordinates.add(fromLngLat(1.0, 1.0))
        val line2 = fromLngLats(coordinates)
        val sliced = lineSlice(start, stop, line2)
        TestCase.assertNotNull(sliced)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfLineSliceRoute1() {
        val start = fromLngLat(-79.0850830078125, 37.60117623656667)
        val stop = fromLngLat(-77.7667236328125, 38.65119833229951)
        val route1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ROUTE_ONE))
        val sliced = lineSlice(start, stop, route1)
        TestCase.assertNotNull(sliced)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfLineSliceRoute2() {
        val start = fromLngLat(-112.60660171508789, 45.96021963947196)
        val stop = fromLngLat(-111.97265625, 48.84302835299516)
        val route2 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ROUTE_TWO))
        val sliced = lineSlice(start, stop, route2)
        TestCase.assertNotNull(sliced)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testTurfLineSliceVertical() {
        val start = fromLngLat(-121.25447809696198, 38.70582415504791)
        val stop = fromLngLat(-121.25447809696198, 38.70634324369764)
        val vertical = Feature.fromJson(loadJsonFixture(LINE_SLICE_VERTICAL))
        val sliced = lineSlice(start, stop, vertical)
        TestCase.assertNotNull(sliced)

        // No duplicated coords
        Assert.assertEquals(2, sliced.coordinates().size.toLong())

        // Vertical slice does not collapse to 1st coord
        Assert.assertNotEquals(sliced.coordinates()[0], sliced.coordinates()[1])
    }

    /*
   * Point on line test
   */
    @Test
    @Throws(Exception::class)
    fun pointOnLine_throwLineMustContainTwoOrMorePoints() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(
            CoreMatchers.startsWith(
                "Turf nearestPointOnLine requires a List of Points made up of at least"
                        + " 2 coordinates."
            )
        )
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45717525482178, 37.72003306385638))
        nearestPointOnLine(line[0]!!, line)
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLineFirstPoint() {
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45717525482178, 37.72003306385638))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val pt = fromLngLat(-122.45717525482178, 37.72003306385638)
        val snappedFeature = nearestPointOnLine(pt, line)
        val snapped = snappedFeature.geometry() as Point?
        // pt on start does not move
        Assert.assertEquals(pt, snapped)
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsBehindFirstPoint() {
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45717525482178, 37.72003306385638))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val first = line[0]
        val pts: MutableList<Point> = ArrayList()
        pts.add(fromLngLat(-122.45717525482178, 37.72009306385638))
        pts.add(fromLngLat(-122.45717525482178, 37.82009306385638))
        pts.add(fromLngLat(-122.45716525482177, 37.72009306385638))
        pts.add(fromLngLat(-122.45516525482178, 37.72009306385638))
        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry() as Point?
            // pt behind start moves to first vertex
            Assert.assertEquals(first, snapped)
        }
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsInFrontOfLastPoint() {
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45616137981413, 37.72125936929241))
        line.add(fromLngLat(-122.45717525482178, 37.72003306385638))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val last = line[2]
        val pts: MutableList<Point> = ArrayList()
        pts.add(fromLngLat(-122.45696067810057, 37.7181405249708))
        pts.add(fromLngLat(-122.4573630094528, 37.71813203814049))
        pts.add(fromLngLat(-122.45730936527252, 37.71797927502795))
        pts.add(fromLngLat(-122.45718061923981, 37.71704571582896))
        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry() as Point?
            // pt behind start moves to last vertex
            Assert.assertEquals(last, snapped)
        }
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsOnJoints() {
        val line1: MutableList<Point> = ArrayList()
        line1.add(fromLngLat(-122.45616137981413, 37.72125936929241))
        line1.add(fromLngLat(-122.45717525482178, 37.72003306385638))
        line1.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val line2: MutableList<Point> = ArrayList()
        line2.add(fromLngLat(26.279296875, 31.728167146023935))
        line2.add(fromLngLat(21.796875, 32.69486597787505))
        line2.add(fromLngLat(18.80859375, 29.99300228455108))
        line2.add(fromLngLat(12.919921874999998, 33.137551192346145))
        line2.add(fromLngLat(10.1953125, 35.60371874069731))
        line2.add(fromLngLat(4.921875, 36.527294814546245))
        line2.add(fromLngLat(-1.669921875, 36.527294814546245))
        line2.add(fromLngLat(-5.44921875, 34.74161249883172))
        line2.add(fromLngLat(-8.7890625, 32.99023555965106))
        val line3: MutableList<Point> = ArrayList()
        line3.add(fromLngLat(-0.10919809341430663, 51.52204224896724))
        line3.add(fromLngLat(-0.10923027992248535, 51.521942114455435))
        line3.add(fromLngLat(-0.10916590690612793, 51.52186200668747))
        line3.add(fromLngLat(-0.10904788970947266, 51.52177522311313))
        line3.add(fromLngLat(-0.10886549949645996, 51.521601655468345))
        line3.add(fromLngLat(-0.10874748229980469, 51.52138135712038))
        line3.add(fromLngLat(-0.10855436325073242, 51.5206870765674))
        line3.add(fromLngLat(-0.10843634605407713, 51.52027984939518))
        line3.add(fromLngLat(-0.10839343070983887, 51.519952729849024))
        line3.add(fromLngLat(-0.10817885398864746, 51.51957887606202))
        line3.add(fromLngLat(-0.10814666748046874, 51.51928513164789))
        line3.add(fromLngLat(-0.10789990425109863, 51.518624199789016))
        line3.add(fromLngLat(-0.10759949684143065, 51.51778299991493))
        val lines: MutableList<List<Point>> = ArrayList()
        lines.add(line1)
        lines.add(line2)
        lines.add(line3)
        for (line in lines) {
            val linePoint: MutableList<Point?> = ArrayList()
            for (pt in line) {
                linePoint.add(
                    fromLngLat(pt.longitude(), pt.latitude())
                )
            }
            for (pt in line) {
                val snappedFeature = nearestPointOnLine(pt, linePoint)
                val snapped = snappedFeature.geometry() as Point?
                // pt on joint stayed in place
                Assert.assertEquals(pt, snapped)
            }
        }
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsOnTopOfLine() {
        val line: MutableList<Point> = ArrayList()
        line.add(fromLngLat(-0.10919809341430663, 51.52204224896724))
        line.add(fromLngLat(-0.10923027992248535, 51.521942114455435))
        line.add(fromLngLat(-0.10916590690612793, 51.52186200668747))
        line.add(fromLngLat(-0.10904788970947266, 51.52177522311313))
        line.add(fromLngLat(-0.10886549949645996, 51.521601655468345))
        line.add(fromLngLat(-0.10874748229980469, 51.52138135712038))
        line.add(fromLngLat(-0.10855436325073242, 51.5206870765674))
        line.add(fromLngLat(-0.10843634605407713, 51.52027984939518))
        line.add(fromLngLat(-0.10839343070983887, 51.519952729849024))
        line.add(fromLngLat(-0.10817885398864746, 51.51957887606202))
        line.add(fromLngLat(-0.10814666748046874, 51.51928513164789))
        line.add(fromLngLat(-0.10789990425109863, 51.518624199789016))
        line.add(fromLngLat(-0.10759949684143065, 51.51778299991493))
        val dist: Double = length(LineString.fromLngLats(line), TurfConstants.UNIT_MILES)
        val increment = dist / 10
        for (i in 0..9) {
            val pt: Point = along(
                LineString.fromLngLats(line), increment * i, TurfConstants.UNIT_MILES
            )
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry() as Point
            val shift = distance(pt, snapped!!, TurfConstants.UNIT_MILES)

            // pt did not shift far
            Assert.assertTrue(shift < 0.000001)
        }
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointAlongLine() {
        val line: MutableList<Point> = ArrayList()
        line.add(fromLngLat(-122.45717525482178, 37.7200330638563))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val pt: Point = along(
            LineString.fromLngLats(line), 0.019, TurfConstants.UNIT_MILES
        )
        val snappedFeature = nearestPointOnLine(pt, line)
        val snapped = snappedFeature.geometry() as Point
        val shift = distance(pt, snapped, TurfConstants.UNIT_MILES)

        // pt did not shift far
        Assert.assertTrue(shift < 0.00001)
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsOnSidesOfLines() {
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45616137981413, 37.72125936929241))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val first = line[0]
        val last = line[1]
        val pts: MutableList<Point> = ArrayList()
        pts.add(fromLngLat(-122.45702505111694, 37.71881098149625))
        pts.add(fromLngLat(-122.45733618736267, 37.719235317933844))
        pts.add(fromLngLat(-122.45686411857605, 37.72027068864082))
        pts.add(fromLngLat(-122.45652079582213, 37.72063561093274))
        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line)
            val snapped = snappedFeature.geometry() as Point?
            // pt did not snap to first vertex
            Assert.assertNotEquals(snapped, first)
            // pt did not snap to last vertex
            Assert.assertNotEquals(snapped, last)
        }
    }

    @Test
    @Throws(TurfException::class)
    fun testTurfPointOnLinePointsOnSidesOfLinesCustomUnit() {
        val line: MutableList<Point?> = ArrayList()
        line.add(fromLngLat(-122.45616137981413, 37.72125936929241))
        line.add(fromLngLat(-122.45717525482178, 37.718242366859215))
        val first = line[0]
        val last = line[1]
        val pts: MutableList<Point> = ArrayList()
        pts.add(fromLngLat(-122.45702505111694, 37.71881098149625))
        pts.add(fromLngLat(-122.45733618736267, 37.719235317933844))
        pts.add(fromLngLat(-122.45686411857605, 37.72027068864082))
        pts.add(fromLngLat(-122.45652079582213, 37.72063561093274))
        for (pt in pts) {
            val snappedFeature = nearestPointOnLine(pt, line, TurfConstants.UNIT_MILES)
            val snapped = snappedFeature.geometry() as Point?
            // pt did not snap to first vertex
            Assert.assertNotEquals(snapped, first)
            // pt did not snap to last vertex
            Assert.assertNotEquals(snapped, last)
        }
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineSliceAlongLine1() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry() as LineString?
        val start = 500.0
        val stop = 750.0
        val start_point = along(lineStringLine1!!, start, TurfConstants.UNIT_MILES)
        val end_point = along(lineStringLine1, stop, TurfConstants.UNIT_MILES)
        val sliced = lineSliceAlong(line1, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            end_point.coordinates()
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineSliceAlongOvershootLine1() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry() as LineString?
        val start = 500.0
        val stop = 1500.0
        val start_point = along(lineStringLine1!!, start, TurfConstants.UNIT_MILES)
        val end_point = along(lineStringLine1, stop, TurfConstants.UNIT_MILES)
        val sliced = lineSliceAlong(line1, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            end_point.coordinates()
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineSliceAlongRoute1() {
        val route1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_ROUTE_ONE))
        val lineStringRoute1 = route1.geometry() as LineString?
        val start = 500.0
        val stop = 750.0
        val start_point = along(lineStringRoute1!!, start, TurfConstants.UNIT_MILES)
        val end_point = along(lineStringRoute1, stop, TurfConstants.UNIT_MILES)
        val sliced = lineSliceAlong(route1, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            end_point.coordinates()
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineSliceAlongRoute2() {
        val route2 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_ROUTE_TWO))
        val lineStringRoute2 = route2.geometry() as LineString?
        val start = 25.0
        val stop = 50.0
        val start_point = along(lineStringRoute2!!, start, TurfConstants.UNIT_MILES)
        val end_point = along(lineStringRoute2, stop, TurfConstants.UNIT_MILES)
        val sliced = lineSliceAlong(route2, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            end_point.coordinates()
        )
    }

    @Test
    @Throws(Exception::class)
    fun testLineAlongStartLongerThanLength() {
        thrown.expect(TurfException::class.java)
        thrown.expectMessage(CoreMatchers.startsWith("Start position is beyond line"))
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val start = 500000.0
        val stop = 800000.0
        lineSliceAlong(line1, start, stop, TurfConstants.UNIT_MILES)
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testLineAlongStopLongerThanLength() {
        val line1 = Feature.fromJson(loadJsonFixture(LINE_SLICE_ALONG_LINE_ONE))
        val lineStringLine1 = line1.geometry() as LineString?
        val start = 500.0
        val stop = 800000.0
        val start_point = along(lineStringLine1!!, start, TurfConstants.UNIT_MILES)
        val lineCoordinates = lineStringLine1.coordinates()
        val sliced = lineSliceAlong(line1, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            lineCoordinates[lineCoordinates.size - 1].coordinates()
        )
    }

    @Test
    @Throws(IOException::class, TurfException::class)
    fun testShortLine() {

        // Distance between points is about 186 miles
        val lineStringLine1 = fromLngLats(
            Arrays.asList(
                fromLngLat(113.99414062499999, 22.350075806124867),
                fromLngLat(116.76269531249999, 23.241346102386135)
            )
        )
        val start = 50.0
        val stop = 100.0
        val start_point = along(lineStringLine1, start, TurfConstants.UNIT_MILES)
        val end_point = along(lineStringLine1, stop, TurfConstants.UNIT_MILES)
        val sliced = lineSliceAlong(lineStringLine1, start, stop, TurfConstants.UNIT_MILES)
        Assert.assertEquals(
            sliced.coordinates()[0].coordinates(),
            start_point.coordinates()
        )
        Assert.assertEquals(
            sliced.coordinates()[sliced.coordinates().size - 1].coordinates(),
            end_point.coordinates()
        )
    }

    companion object {
        private const val LINE_SLICE_ONE = "turf-line-slice/line1.geojson"
        private const val LINE_SLICE_ROUTE_ONE = "turf-line-slice/route1.geojson"
        private const val LINE_SLICE_ROUTE_TWO = "turf-line-slice/route2.geojson"
        private const val LINE_SLICE_VERTICAL = "turf-line-slice/vertical.geojson"
        private const val LINE_SLICE_ALONG_LINE_ONE = "turf-line-slice-along/line1.geojson"
        private const val LINE_SLICE_ALONG_ROUTE_ONE = "turf-line-slice-along/route1.geojson"
        private const val LINE_SLICE_ALONG_ROUTE_TWO = "turf-line-slice-along/route2.geojson"
    }
}