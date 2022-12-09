package com.mapbox.turf.models

/**
 * if the lines intersect, the result contains the x and y of the intersection (treating the lines
 * as infinite) and booleans for whether line segment 1 or line segment 2 contain the point.
 *
 * @see [Good example of how this class works written in JavaScript](http://jsfiddle.net/justin_c_rounds/Gd2S2/light/)
 *
 * @since 1.2.0
 */
class LineIntersectsResult private constructor(
    private val horizontalIntersection: Double?,
    private val verticalIntersection: Double?,
    private val onLine1: Boolean,
    private val onLine2: Boolean
) {
    /**
     * If the lines intersect, use this method to get the intersecting point `X` value.
     *
     * @return the `X` value where the lines intersect
     * @since 1.2.0
     */
    fun horizontalIntersection(): Double? {
        return horizontalIntersection
    }

    /**
     * If the lines intersect, use this method to get the intersecting point `Y` value.
     *
     * @return the `Y` value where the lines intersect
     * @since 1.2.0
     */
    fun verticalIntersection(): Double? {
        return verticalIntersection
    }

    /**
     * Determine if the intersecting point lands on line 1 or not.
     *
     * @return true if the intersecting point is located on line 1, otherwise false
     * @since 1.2.0
     */
    fun onLine1(): Boolean {
        return onLine1
    }

    /**
     * Determine if the intersecting point lands on line 2 or not.
     *
     * @return true if the intersecting point is located on line 2, otherwise false
     * @since 1.2.0
     */
    fun onLine2(): Boolean {
        return onLine2
    }

    override fun toString(): String {
        return ("LineIntersectsResult{"
                + "horizontalIntersection=" + horizontalIntersection + ", "
                + "verticalIntersection=" + verticalIntersection + ", "
                + "onLine1=" + onLine1 + ", "
                + "onLine2=" + onLine2
                + "}")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj is LineIntersectsResult) {
            val that = obj
            return ((if (horizontalIntersection == null) that.horizontalIntersection() == null else horizontalIntersection == that.horizontalIntersection())
                    && (if (verticalIntersection == null) that.verticalIntersection() == null else verticalIntersection == that.verticalIntersection())
                    && onLine1 == that.onLine1()
                    && onLine2 == that.onLine2())
        }
        return false
    }

    override fun hashCode(): Int {
        var hashCode = 1
        hashCode *= 1000003
        hashCode = hashCode xor (horizontalIntersection?.hashCode() ?: 0)
        hashCode *= 1000003
        hashCode = hashCode xor (verticalIntersection?.hashCode() ?: 0)
        hashCode *= 1000003
        hashCode = hashCode xor if (onLine1) 1231 else 1237
        hashCode *= 1000003
        hashCode = hashCode xor if (onLine2) 1231 else 1237
        return hashCode
    }

    /**
     * Convert current instance values into another Builder to quickly change one or more values.
     *
     * @return a new instance of [LineIntersectsResult] using the newly defined values
     * @since 3.0.0
     */
    fun toBuilder(): Builder {
        return Builder(this)
    }

    /**
     * Build a new [LineIntersectsResult] instance and define its features by passing in
     * information through the offered methods.
     *
     * @since 3.0.0
     */
    class Builder {
        private var horizontalIntersection: Double? = null
        private var verticalIntersection: Double? = null
        private var onLine1 = false
        private var onLine2 = false

        internal constructor() {}
        constructor(source: LineIntersectsResult) {
            horizontalIntersection = source.horizontalIntersection()
            verticalIntersection = source.verticalIntersection()
            onLine1 = source.onLine1()
            onLine2 = source.onLine2()
        }

        /**
         * If the lines intersect, use this method to get the intersecting point `X` value.
         *
         * @param horizontalIntersection the x coordinates intersection point
         * @return the `X` value where the lines intersect
         * @since 3.0.0
         */
        fun horizontalIntersection(horizontalIntersection: Double?): Builder {
            this.horizontalIntersection = horizontalIntersection
            return this
        }

        /**
         * If the lines intersect, use this method to get the intersecting point `Y` value.
         *
         * @param verticalIntersection the y coordinates intersection point
         * @return the `Y` value where the lines intersect
         * @since 3.0.0
         */
        fun verticalIntersection(verticalIntersection: Double?): Builder {
            this.verticalIntersection = verticalIntersection
            return this
        }

        /**
         * Determine if the intersecting point lands on line 1 or not.
         *
         * @param onLine1 true if the points land on line one, else false
         * @return true if the intersecting point is located on line 1, otherwise false
         * @since 3.0.0
         */
        fun onLine1(onLine1: Boolean): Builder {
            this.onLine1 = onLine1
            return this
        }

        /**
         * Determine if the intersecting point lands on line 2 or not.
         *
         * @param onLine2 true if the points land on line two, else false
         * @return true if the intersecting point is located on line 2, otherwise false
         * @since 3.0.0
         */
        fun onLine2(onLine2: Boolean): Builder {
            this.onLine2 = onLine2
            return this
        }

        /**
         * Builds a new instance of a [LineIntersectsResult] class.
         *
         * @return a new instance of [LineIntersectsResult]
         * @since 3.0.0
         */
        fun build(): LineIntersectsResult {
            var missing = ""
            if (onLine1 == null) {
                missing += " onLine1"
            }
            if (onLine2 == null) {
                missing += " onLine2"
            }
            check(missing.isEmpty()) { "Missing required properties:$missing" }
            return LineIntersectsResult(
                horizontalIntersection,
                verticalIntersection,
                onLine1,
                onLine2
            )
        }
    }

    companion object {
        /**
         * Builds a new instance of a lineIntersection. This class is mainly used internally for other
         * turf objects to recall memory when performing calculations.
         *
         * @return [LineIntersectsResult.Builder] for creating a new instance
         * @since 3.0.0
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}