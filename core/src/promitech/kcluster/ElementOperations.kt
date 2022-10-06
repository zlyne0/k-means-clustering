package promitech.kcluster

/**
 * Allow points to be immutable.
 * Centroid is not immutable.
 */
interface ElementOperations<KP, Centroid> {
    fun createCentroid(): Centroid
    fun createCentroid(point: KP): Centroid
    fun createCentroidFromCentroid(centroid: Centroid): Centroid
    fun distance(point: KP, centroid: Centroid): Double
    fun distanceBetweenPoints(p1: KP, p2: KP): Double
    fun setPosition(centroid: Centroid, point: KP)

    /**
     * Set centroid a average distance between argument centroid and point
     */
    fun avr(centroid: Centroid, point: KP)
    fun isTheSamePosition(c1: Centroid, c2: Centroid): Boolean
    fun setPosition2(centroid: Centroid, c2: Centroid)
}